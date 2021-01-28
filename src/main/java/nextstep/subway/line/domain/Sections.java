package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	public static final String ALREADY_REGISTERED_STATIONS = "이미 등록된 지하철역 입니다.";
	public static final String NO_CONNECTABLE_SECTION = "연결 할 수 있는 구간이 없습니다.";

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Section> sections = new ArrayList<>();

	public void add(Section section) {
		List<Station> stations = getStations();
		validateAddableSection(stations, section);
		updateSection(stations, section);

		sections.add(section);
	}

	public List<Section> getSections() {
		return Collections.unmodifiableList(sections);
	}

	public List<Station> getStations() {
		return sections.stream()
			.sorted()
			.map(Section::getStations)
			.flatMap(Collection::stream)
			.distinct()
			.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
	}

	public void remove(Station station) {
		Optional<Section> upLineSection = findSectionByUpStation(station);
		Optional<Section> downLineSection = findSectionByDownStation(station);

		upLineSection.ifPresent(sections::remove);
		downLineSection.ifPresent(sections::remove);

		if (upLineSection.isPresent() && downLineSection.isPresent()) {
			connectSections(upLineSection.get(), downLineSection.get());
		}
	}

	private Optional<Section> findSectionByUpStation(Station station) {
		return sections.stream()
			.filter(section -> section.isUpStation(station))
			.findFirst();
	}

	private Optional<Section> findSectionByDownStation(Station station) {
		return sections.stream()
			.filter(section -> section.isDownStation(station))
			.findFirst();
	}

	private void connectSections(Section upSection, Section downSection) {
		Line line = upSection.getLine();
		Station newUpStation = downSection.getUpStation();
		Station newDownStation = upSection.getDownStation();
		int newDistance = upSection.plusDistance(downSection);

		add(
			Section.builder()
				.line(line)
				.upStation(newUpStation)
				.downStation(newDownStation)
				.distance(newDistance)
				.build()
		);
	}

	private void updateSection(List<Station> stations, Section section) {
		if (stations.contains(section.getUpStation())) {
			updateUpStation(section);
			return;
		}

		if (stations.contains(section.getDownStation())) {
			updateDownStation(section);
		}
	}

	private void updateUpStation(Section section) {
		sections.stream()
			.filter(it -> it.isSameUpStation(section))
			.findFirst()
			.ifPresent(it -> it.updateUpStation(section));
	}

	private void updateDownStation(Section section) {
		sections.stream()
			.filter(it -> it.isSameDownStation(section))
			.findFirst()
			.ifPresent(it -> it.updateDownStation(section));
	}

	private void validateAddableSection(List<Station> stations, Section section) {
		Station upStation = section.getUpStation();
		Station downStation = section.getDownStation();

		if (stations.contains(upStation) && stations.contains(downStation)) {
			throw new IllegalArgumentException(ALREADY_REGISTERED_STATIONS);
		}

		if (!stations.isEmpty() && !stations.contains(upStation) && !stations.contains(downStation)) {
			throw new IllegalArgumentException(NO_CONNECTABLE_SECTION);
		}
	}
}
