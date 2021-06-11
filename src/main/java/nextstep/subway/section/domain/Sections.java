package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	private static final String INVALID_SECTION_MESSAGE = "노선 설정이 잘못되었습니다.";
	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	private final List<Section> sections = new ArrayList<>();

	public Sections() {
	}

	public void addSection(Section section) {
		this.sections.add(section);
	}

	public void removeSection(Section section) {
		this.sections.remove(section);
	}

	public List<Station> getOrderedStations() {
		Optional<Section> currentSection = this.findUpStationEndPointSection();
		Stream<Station> stations = this.getStationStream(currentSection);

		do {
			currentSection = this.nextSection(currentSection);
			stations = Stream.concat(stations,
				Optional.of(this.getStationStream(currentSection)).orElse(Stream.empty()))
				.filter(Objects::nonNull)
				.distinct();
		} while (hasNext(currentSection));

		return stations.collect(Collectors.toList());
	}

	private Stream<Station> getStationStream(Optional<Section> section) {
		return section.map(value -> Stream.of(value.getUpStation(), value.getDownStation())).orElseGet(Stream::empty);
	}

	private boolean hasNext(Optional<Section> currentSection) {
		return currentSection.filter(value -> this.sections.stream()
			.anyMatch(section -> section.getUpStation().equals(value.getDownStation()))).isPresent();
	}

	private Optional<Section> nextSection(Optional<Section> currentSection) {
		return currentSection.flatMap(value -> this.sections.stream()
			.filter(section -> section.getUpStation().equals(value.getDownStation()))
			.findAny());

	}

	private Optional<Section> findUpStationEndPointSection() {
		List<Station> downStations = this.sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());

		return this.sections.stream()
			.filter(section -> !downStations.contains(section.getUpStation()))
			.findFirst();
	}

}
