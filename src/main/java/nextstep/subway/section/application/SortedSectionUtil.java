package nextstep.subway.section.application;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SortedSectionUtil {

	private final List<Section> sections;
	private final Map<Station, Section> upStationMap;
	private final List<Station> downStations;

	private SortedSectionUtil(final List<Section> sections) {
		this.sections = sections;
		this.upStationMap = groupByUpStation();
		this.downStations = downStations();
	}

	public static SortedSectionUtil of(final List<Section> sections) {
		return new SortedSectionUtil(sections);
	}

	public List<Station> sorted() {
		Optional<Section> firstSection = findFirstSection();
		if (firstSection.isPresent() == false) {
			return Collections.emptyList();
		}
		return getSortedStations(firstSection.get());
	}

	private Map<Station, Section> groupByUpStation() {
		return sections.stream()
					   .collect(Collectors.toMap(section -> section.upStation(), Function.identity()));
	}

	private List<Station> downStations() {
		return sections.stream()
					   .map(section -> section.downStation())
					   .collect(Collectors.toList());
	}

	private Optional<Section> findFirstSection() {
		return sections.stream()
					   .filter(section -> this.downStations.contains(section.upStation()) == false)
					   .findFirst();
	}

	private List<Station> getSortedStations(Section nextSection) {
		List<Station> stations = new ArrayList<>();
		stations.add(nextSection.upStation());
		do {
			stations.add(nextSection.downStation());
			nextSection = upStationMap.get(nextSection.downStation());
		} while (nextSection != null);
		return stations;
	}
}
