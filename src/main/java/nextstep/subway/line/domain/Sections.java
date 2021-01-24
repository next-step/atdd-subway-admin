package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Section> sections = new ArrayList<>();

	public void add(Section section) {
		updateSection(section);
		sections.add(section);
	}

	public List<Section> getSections() {
		return Collections.unmodifiableList(sections);
	}

	public List<Station> getStations() {
		return Collections.unmodifiableList(
			sections.stream()
				.sorted()
				.map(Section::getStations)
				.flatMap(Collection::stream)
				.distinct()
				.collect(Collectors.toList())
		);
	}

	private void updateSection(Section section) {
		List<Station> stations = getStations();

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
			.filter(it -> it.isUpStation(section.getUpStation()))
			.findFirst()
			.ifPresent(it -> it.updateUpStation(section.getDownStation(), section.getDistance()));
	}

	private void updateDownStation(Section section) {
		sections.stream()
			.filter(it -> it.isDownStation(section.getDownStation()))
			.findFirst()
			.ifPresent(it -> it.updateDownStation(section.getUpStation(), section.getDistance()));
	}
}
