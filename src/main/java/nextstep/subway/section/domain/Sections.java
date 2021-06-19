package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@Transactional
public class Sections {
	private static final int REMOVAL_SECTION_INDEX = 0;
	private static final int REMAINED_SECTION_INDEX = 1;
	private static final int NO_NEED_TO_JOIN_SECTION_SIZE = 1;

	@OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public List<Station> getStationsInSections() {
		List<Station> stationsInOrder = new ArrayList<>();
		Station firstStation = findFirstStation();
		stationsInOrder.add(firstStation);

		while (doesExistNextStation(firstStation)) {
			firstStation = findNextStation(firstStation);
			stationsInOrder.add(firstStation);
		}
		return stationsInOrder;
	}

	private Station findNextStation(Station station) {
		return sections.stream().filter(section -> section.getUpStation().equals(station))
				.findFirst().get()
				.getDownStation();
	}

	private boolean doesExistNextStation(Station station) {
		return sections.stream().anyMatch(section -> section.getUpStation().equals(station));
	}

	private Station findFirstStation() {
		Station firstStation = null;
		for (Section section : sections) {
			firstStation = findRightUpStation(firstStation, section);
		}
		return firstStation;
	}

	private Station findRightUpStation(Station firstStation, Section section) {
		if (section.getDownStation().equals(firstStation) || firstStation == null) {
			firstStation = section.getUpStation();
		}
		return firstStation;
	}

	public void addSection(Section section) {
		sections.stream().filter(existedSection -> existedSection.getUpStation().equals(section.getUpStation()))
				.findFirst()
				.ifPresent(existedSection -> existedSection.updateUpStation(section.getDownStation(), section.getDistance()));

		sections.stream().filter(existedSection -> existedSection.getDownStation().equals(section.getDownStation()))
				.findFirst()
				.ifPresent(existedSection -> existedSection.updateDownStation(section.getUpStation(), section.getDistance()));

		sections.add(section);
	}

	public void deleteSectionByStationId(Long stationId) {
		List<Section> sectionsIncludingStation = sections.stream().filter(section -> section.doesIncludeStation(stationId))
				.collect(Collectors.toList());

		if (sectionsIncludingStation.size() == NO_NEED_TO_JOIN_SECTION_SIZE) {
			sections.remove(sectionsIncludingStation.get(REMOVAL_SECTION_INDEX));
			return;
		}

		Section removalSection = sectionsIncludingStation.get(REMAINED_SECTION_INDEX);
		Station remainedStation = removalSection.getRemainedStation(stationId);
		sectionsIncludingStation.get(REMOVAL_SECTION_INDEX).joinSection(stationId, remainedStation, removalSection.getDistance());
		sections.remove(removalSection);
	}
}
