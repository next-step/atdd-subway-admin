package nextstep.subway.domain.line;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.InvalidSectionAddException;
import nextstep.subway.exception.InvalidSectionRemoveException;

@Embeddable
public class Sections {

	private static final String NOT_INCLUDE_UP_DOWN_STATION_ERROR_MESSAGE = "등록할 수 없는 구간입니다.";
	private static final String SAME_UP_DOWN_STATION_ERROR_MESSAGE = "이미 등록된 구간입니다.";
	private static final int MINIMUM_SECTION_COUNT = 1;
	private static final String STATION_NOT_EXIST_MESSAGE_FORMAT = "%s은 존재하지 없는 역입니다.";
	private static final String INVALID_MINIMUM_SECTION_COUNT_MESSAGE = "구간이 하나인 노선에서는 제거할 수 없습니다.";
	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	@Transient
	private Map<Station, Section> sectionByUpStationMap;

	@Transient
	private Map<Station, Section> sectionByDownStationMap;

	protected Sections() {
	}

	public Sections(List<Section> sections) {
		this.sections = sections;
	}

	public static Sections from(Section section) {
		List<Section> sections = new LinkedList<>();
		sections.add(section);
		return new Sections(sections);
	}

	public List<Section> getSections() {
		return sections;
	}

	private List<Station> allUpStations() {
		return this.sections.stream()
			.map(Section::getUpStation)
			.collect(Collectors.toCollection(LinkedList::new));
	}

	private List<Station> allDownStations() {
		return this.sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public List<Station> allStations() {
		List<Station> stations = new LinkedList<>();
		stations.add(firstUpStation());

		for (int i = 0; i < sections.size(); i++) {
			Station nextStation = sectionByUpStation(stations.get(i)).getDownStation();
			stations.add(nextStation);
		}
		return stations;
	}

	private Section sectionByUpStation(Station station) {
		return this.sections.stream()
			.filter(section -> section.getUpStation().getName().equals(station.getName()))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Section not exist"));
	}

	private Section sectionByUpStationMap(Station station) {
		if (sectionByUpStationMap == null) {
			sectionByUpStationMap = this.sections.stream()
				.collect(Collectors.toMap(Section::getUpStation, section -> section));
		}
		return sectionByUpStationMap.get(station);
	}

	private Section sectionByDownStationMap(Station station) {
		if (sectionByDownStationMap == null) {
			sectionByDownStationMap = this.sections.stream()
				.collect(Collectors.toMap(Section::getDownStation, section -> section));
		}
		return sectionByDownStationMap.get(station);
	}

	public Station firstUpStation() {
		List<Station> stations = allUpStations();
		stations.removeIf(station -> allDownStations().contains(station));
		return stations.get(0);
	}

	public Station lastDownStation() {
		List<Station> stations = allDownStations();
		stations.removeIf(station -> allUpStations().contains(station));
		return stations.get(0);
	}

	public void connect(Section section, List<Section> sectionsToRearrange) {
		validateAddSection(section);
		SectionConnector connector = SectionConnector.of(section, sectionsToRearrange);
		connector.connect();
		sections.add(section);
	}

	public void remove(Station station) {
		validateRemoveSection(station);
		removeSection(station);
		clearCache();
	}

	public void removeSection(Section section) {
		sections.remove(section);
	}

	private void clearCache() {
		sectionByUpStationMap = null;
		sectionByDownStationMap = null;
	}

	private void removeSection(Station station) {
		Section upSection = sectionByUpStationMap(station);
		Section downSection = sectionByDownStationMap(station);
		SectionRemover.of(this, upSection, downSection).remove();
	}

	private void validateRemoveSection(Station station) {
		if (isNotExistStation(station)) {
			throw new InvalidSectionRemoveException(String.format(STATION_NOT_EXIST_MESSAGE_FORMAT, station.getName()));
		}
		if (invalidSectionMinimumSize()) {
			throw new InvalidSectionRemoveException(INVALID_MINIMUM_SECTION_COUNT_MESSAGE);
		}
	}

	private boolean isExistStation(Station station) {
		return sectionByUpStationMap(station) != null || sectionByDownStationMap(station) != null;
	}

	private boolean isNotExistStation(Station station) {
		return !isExistStation(station);
	}

	private boolean invalidSectionMinimumSize() {
		return sections.size() <= MINIMUM_SECTION_COUNT;
	}

	private void validateAddSection(Section section) {
		validateSameUpDownStation(section);
		validateNotIncludeUpDownStation(section);
	}

	private void validateNotIncludeUpDownStation(Section section) {
		if (notIncludeUpDownStation(section)) {
			throw new InvalidSectionAddException(NOT_INCLUDE_UP_DOWN_STATION_ERROR_MESSAGE);
		}
	}

	private void validateSameUpDownStation(Section section) {
		if (isSameUpDownStation(section)) {
			throw new InvalidSectionAddException(SAME_UP_DOWN_STATION_ERROR_MESSAGE);
		}
	}

	private boolean notIncludeUpDownStation(Section section) {
		return notIncludeDownStation(section) && notIncludeUpStation(section);
	}

	private boolean notIncludeDownStation(Section section) {
		return !allStations().contains(section.getUpStation());
	}

	private boolean notIncludeUpStation(Section section) {
		return !allStations().contains(section.getDownStation());
	}

	private boolean isSameUpDownStation(Section newSection) {
		return sections.stream()
			.filter(section -> section.isSameUpStation(newSection))
			.anyMatch(section -> section.isSameDownStation(newSection));
	}

}
