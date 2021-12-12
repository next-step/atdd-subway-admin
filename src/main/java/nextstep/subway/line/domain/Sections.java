package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.common.exception.BadParameterException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	public static final String EXCEPTION_MESSAGE_SECTION_EXACTLY_EQUAL = "상행역과 하행역이 모두 노선 구간으로 등록되어 있습니다.";
	public static final String EXCEPTION_MESSAGE_SECTION_NOT_INCLUDE = "등록하려는 구간의 상행역과 하행역이 현재 노선 구간에 포함되어 있지 않습니다.";

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	private List<Section> sections = new ArrayList<>();

	public Sections() {
	}

	public Sections(List<Section> sections) {
		this.sections = sections;
	}

	public List<Section> get() {
		return sections;
	}

	public void add(Section section) {
		if (!sections.isEmpty()) {
			validate(section);
			updateConnectedSection(section);
		}
		sections.add(section);
	}

	private void validate(Section other) {
		if (hasNoConnectedStation(other)) {
			throw new BadParameterException(EXCEPTION_MESSAGE_SECTION_NOT_INCLUDE);
		}

		if (hasExactlyEqualSection(other)) {
			throw new BadParameterException(EXCEPTION_MESSAGE_SECTION_EXACTLY_EQUAL);
		}
	}

	private boolean hasExactlyEqualSection(Section other) {
		Optional<Section> connectedByUpStation = findSectionConnectedStation(other.getUpStation());
		Optional<Section> connectedByDownStation = findSectionConnectedStation(other.getDownStation());
		return connectedByUpStation.isPresent() && connectedByDownStation.isPresent();
	}

	private boolean hasNoConnectedStation(Section other) {
		Optional<Section> connectedByUpStation = findSectionConnectedStation(other.getUpStation());
		Optional<Section> connectedByDownStation = findSectionConnectedStation(other.getDownStation());
		return !connectedByUpStation.isPresent() && !connectedByDownStation.isPresent();
	}

	private void updateConnectedSection(Section other) {
		sections.stream()
			.filter(section -> section.getUpStation().equals(other.getUpStation()))
			.findFirst()
			.ifPresent(section -> section.updateByUpSection(other));
		sections.stream()
			.filter(it -> it.getDownStation().equals(other.getDownStation()))
			.findFirst()
			.ifPresent(section -> section.updateByDownSection(other));
	}

	private Optional<Section> findSectionConnectedStation(Station station) {
		return sections.stream()
			.filter(it -> it.getUpStation().equals(station) || it.getDownStation().equals(station))
			.findFirst();
	}

	public List<Station> getStationsList() {
		Map<Station, Station> map = new HashMap<>();
		sections.forEach(section -> map.put(section.getUpStation(), section.getDownStation()));

		List<Station> stations = new ArrayList<>();
		Station station = findFirstStation();
		while (map.get(station) != null) {
			stations.add(station);
			station = map.get(station);
		}
		stations.add(station);
		return stations;
	}

	private Station findFirstStation() {
		List<Station> upStations = getUpStations();
		List<Station> downStations = getDownStations();
		return upStations.stream()
			.filter(upStation -> !downStations.contains(upStation))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("잘못된 구간 정보입니다."));
	}

	private Station findFirstStation(List<Section> sections) {
		List<Station> upStations = getUpStations();
		List<Station> downStations = getDownStations();
		return upStations.stream()
			.filter(upStation -> !downStations.contains(upStation))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("잘못된 구간 정보입니다."));
	}

	private Station findLastStation(List<Section> sections) {
		List<Station> upStations = getUpStations();
		List<Station> downStations = getDownStations();
		return downStations.stream()
			.filter(downStation -> !upStations.contains(downStation))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("잘못된 구간 정보입니다."));
	}

	private List<Station> getUpStations() {
		return sections.stream()
			.map(Section::getUpStation)
			.collect(Collectors.toList());
	}

	private List<Station> getDownStations() {
		return sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());
	}

	public boolean contains(Section section) {
		return sections.contains(section);
	}

	public void deleteSectionByStationId(Long stationId) {
		List<Section> sectionsConnected = findSections(stationId);
		if (sectionsConnected.size() == 2) {
			addSectionComposedFirstAndLastStation(sectionsConnected);
		}

		for (Section section : sectionsConnected) {
			sections.remove(section);
		}
	}

	private void addSectionComposedFirstAndLastStation(List<Section> sections) {
		Station upStation = findFirstStation(sections);
		Station downStation = findLastStation(sections);
		Integer newDistance = sections.stream()
			.map(section -> section.getDistance().get())
			.reduce(0, Integer::sum);
		this.sections.add(new Section(upStation, downStation, newDistance));
	}

	private List<Section> findSections(Long stationId) {
		return sections.stream()
			.filter(getSectionPredicate(stationId))
			.collect(Collectors.toList());
	}

	private Predicate<Section> getSectionPredicate(Long stationId) {
		return section -> section.getUpStation().getId().equals(stationId) || section.getDownStation()
			.getId()
			.equals(stationId);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Sections sections1 = (Sections)o;
		return Objects.equals(sections, sections1.sections);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sections);
	}
}
