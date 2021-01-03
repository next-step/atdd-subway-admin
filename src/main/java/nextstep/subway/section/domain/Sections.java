package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
	private List<Section> sections = new ArrayList<>();

	public Sections() {
	}

	public Sections(List<Section> sections) {
		this.sections = sections;
	}

	public void add(Section newSection) {
		if (this.sections.isEmpty()) {
			this.sections.add(newSection);
			return;
		}

		Set<Station> upStations = sections.stream()
			  .map(Section::getUpStation)
			  .collect(Collectors.toSet());
		Set<Station> downStations = sections.stream()
			  .map(Section::getDownStation)
			  .collect(Collectors.toSet());

		validateNewSection(newSection, upStations, downStations);

		if (isEndSection(newSection, upStations, downStations)) {
			this.sections.add(newSection);
			return;
		}

		if (isMiddleSection(newSection, upStations, downStations)) {
			addInMiddleOfSection(newSection, upStations, downStations);
		}
	}

	private void validateNewSection(Section newSection, Set<Station> upStations,
		  Set<Station> downStations) {
		if (!isContainedAnyStationInSections(newSection, upStations, downStations)) {
			throw new IllegalArgumentException("연결가능한 구간정보가 없습니다.");
		}

		if (isContainedAllStationsInExistedSection(newSection, upStations, downStations)) {
			throw new IllegalArgumentException("이미 등록된 구간과 중복되거나, 추가할 수 없는 비정상적인 구간입니다.");
		}
	}

	private boolean isContainedAnyStationInSections(Section newSection, Set<Station> upStations,
		  Set<Station> downStations) {
		boolean containedInUpStations = newSection
			  .isContainedUpStationsInExistSections(upStations, downStations);
		boolean containedInDownStations = newSection
			  .isContainedDownStationsInExistSections(upStations, downStations);

		return containedInUpStations || containedInDownStations;
	}

	private boolean isContainedAllStationsInExistedSection(Section newSection,
		  Set<Station> upStations, Set<Station> downStations) {
		boolean containedInUpStations = newSection
			  .isContainedUpStationsInExistSections(upStations, downStations);
		boolean containedInDownStations = newSection
			  .isContainedDownStationsInExistSections(upStations, downStations);

		return containedInUpStations && containedInDownStations;
	}

	private boolean isEndSection(Section newSection, Set<Station> upStations,
		  Set<Station> downStations) {

		//n.up은 둘다 없고, n.down은 up에만 존재
		//n.up은 down에만 존재, n.down은 없음
		return newSection.isEndOfUpSection(upStations, downStations)
			  || newSection.isEndOfDownSection(upStations, downStations);
	}

	private boolean isMiddleSection(Section newSection, Set<Station> upStations,
		  Set<Station> downStations) {
		return newSection.isMiddleOfSection(upStations, downStations);
	}

	public List<Station> stationsInOrder() {
		Section firstUpSection = findFirstUpSection();
		Map<Station, Section> stationMap = this.sections.stream()
			  .collect(Collectors.toMap(Section::getUpStation, Function.identity()));

		List<Station> stations = new ArrayList<>();
		stations.add(firstUpSection.getUpStation());
		stations.add(firstUpSection.getDownStation());

		Section currentSection = firstUpSection;
		while (stations.size() < this.sections.size() + 1) {
			currentSection = stationMap.get(currentSection.getDownStation());
			stations.add(currentSection.getDownStation());
		}

		return stations;
	}

	private Section findFirstUpSection() {
		Map<Station, Section> downStationMap = this.sections.stream()
			  .collect(Collectors.toMap(Section::getDownStation, Function.identity()));

		return this.sections.stream()
			  .filter(section -> !downStationMap.containsKey(section.getUpStation()))
			  .findFirst().orElseThrow(() -> new NoSuchElementException("상행종점역을 찾을 수 없습니다."));
	}

	private void addInMiddleOfSection(Section newSection, Set<Station> upStations,
		  Set<Station> downStations) {
		if (newSection.isLinkedUpStation(upStations, downStations)) {
			addUpSection(newSection);
		}

		if (newSection.isLinkedDownStation(upStations, downStations)) {
			addDownSection(newSection);
		}

		this.sections.add(newSection);
	}

	private void addUpSection(Section newSection) {
		Section targetSection = this.sections.stream()
			  .filter(section -> section.isUpStationEquals(newSection))
			  .findFirst()
			  .orElseThrow(() -> new NoSuchElementException("관련구간을 찾을 수 없습니다."));

		int newDistance = calculateDistance(targetSection, newSection);
		targetSection.changeUpStation(newSection.getDownStation(), newDistance);
	}

	private void addDownSection(Section newSection) {
		Section targetSection = this.sections.stream()
			  .filter(section -> section.isDownStationEquals(newSection))
			  .findFirst()
			  .orElseThrow(() -> new NoSuchElementException("관련구간을 찾을 수 없습니다."));

		int newDistance = calculateDistance(targetSection, newSection);
		targetSection.changeDownStation(newSection.getUpStation(), newDistance);
	}

	private int calculateDistance(Section section, Section newSection) {
		int distance = section.calculateDistance(newSection);
		if (distance < 1) {
			throw new IllegalArgumentException("추가하려는 구간의 길이는 기존 구간의 길이보다 작아야 합니다.");
		}

		return distance;
	}

	public List<Section> getSections() {
		return sections;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Sections sections1 = (Sections) o;
		if (this.sections.size() != sections1.sections.size()) {
			return false;
		}

		return this.sections.containsAll(sections1.sections);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sections);
	}
}
