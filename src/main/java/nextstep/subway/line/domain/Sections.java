package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	protected Sections() {
	}

	public void addSection(Section requestSection, Line line) {
		if (!isEmpty()) {
			adjustSection(requestSection);
		}
		sections.add(requestSection);
		requestSection.assignLine(line);
	}

	public void adjustSection(Section requestSection) {
		List<Section> orderedSection = getOrderSections();

		if(isOutBoundInsertable(orderedSection,requestSection)){
			return;
		}

		validateNotExistSection(requestSection);
		Optional<Section> matchUpSection = findMatchUpSection(requestSection);
		if (matchUpSection.isPresent()) {
			sliceUpSection(matchUpSection.get(), requestSection);
			return;
		}

		Optional<Section> matchDownSection = findMatchDownSection(requestSection);
		if (matchDownSection.isPresent()) {
			sliceDownSection(matchDownSection.get(), requestSection);
			return;
		}
		throw new IllegalStateException("기존에 존재하는 역을 포함하여 구간 추가를 요청해야 합니다.");
	}

	private boolean isOutBoundInsertable(List<Section> orderedSection, Section requestSection) {
		// 상행 종점 앞에 구간 추가하는 경우
		if (isAheadOfBeginningSection(orderedSection,requestSection)) {
			return true;
		}

		// 하행 종점 뒤에 구간 추가하는 경우
		if (isBehindLastSection(orderedSection,requestSection)) {
			return true;
		}
		return false;
	}

	private boolean isAheadOfBeginningSection(List<Section> orderedSection, Section requestSection) {
		Section startSection = orderedSection.get(0);
		return startSection.matchUpStation(requestSection.getDownStation());
	}

	private boolean isBehindLastSection(List<Section> orderedSection, Section requestSection) {
		Section lastSection = orderedSection.get(orderedSection.size()-1);
		return lastSection.matchDownStation(requestSection.getUpStation());
	}

	private Set<Station> getStations() {
		Set<Station> stations = new HashSet<>();
		for (Section section : sections) {
			stations.add(section.getUpStation());
			stations.add(section.getDownStation());
		}
		return stations;
	}

	private void validateNotExistSection(Section requestSection) {
		Set<Station> stations = getStations();
		if (stations.containsAll(Arrays.asList(requestSection.getUpStation(), requestSection.getDownStation()))) {
			throw new IllegalArgumentException("이미 등록된 구간 정보입니다.");
		}
	}

	private Optional<Section> findMatchUpSection(Section requestSection) {
		return sections.stream()
			.filter(section -> section.matchUpStation(requestSection.getUpStation()))
			.findAny();
	}

	private void sliceUpSection(Section currentSection, Section sliceSection) {
		validateInsertableLengthBetween(currentSection, sliceSection);
		currentSection.splitUpSection(sliceSection);
	}

	private Optional<Section> findMatchDownSection(Section requestSection) {
		return sections.stream()
			.filter(section -> section.matchDownStation(requestSection.getDownStation()))
			.findAny();
	}

	private void sliceDownSection(Section currentSection, Section sliceSection) {
		validateInsertableLengthBetween(currentSection, sliceSection);
		currentSection.splitDownSection(sliceSection);
	}

	private void validateInsertableLengthBetween(Section currentSection, Section requestSection) {
		if (currentSection.getDistance() <= requestSection.getDistance()) {
			throw new IllegalArgumentException("기존 역 사이 길이와 같거나 긴 구간은 등록이 불가합니다.");
		}
	}

	public List<Station> getOrderedStations() {
		List<Section> orderedSections = getOrderSections();
		return converToStations(orderedSections);
	}

	private List<Station> converToStations(List<Section> orderedSections) {
		List<Station> stations = new ArrayList<>();
		for (Section section : orderedSections) {
			stations.add(section.getUpStation());
		}
		stations.add(orderedSections.get(orderedSections.size() - 1).getDownStation());
		return stations;
	}

	private List<Section> getOrderSections() {
		Section startSection = findStartSection();

		Map<Station, Section> upStationAndSectionRoute = getSectionRoute();
		List<Section> orderedSections = new ArrayList<>();
		Section nextSection = startSection;
		while (nextSection != null) {
			orderedSections.add(nextSection);
			Station curDownStation = nextSection.getDownStation();
			nextSection = upStationAndSectionRoute.get(curDownStation);
		}
		return orderedSections;
	}

	private Map<Station, Section> getSectionRoute() {
		return sections.stream()
			.collect(Collectors.toMap(Section::getUpStation, section -> section, (stationKey1, stationKey2) -> stationKey1, HashMap::new));
	}

	private Section findStartSection() {
		Set<Station> downStations = sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toSet());
		// 전체 상행역 중 하행역이 아닌 상행역 추출(=> 시작점)
		return sections.stream()
			.filter(section -> !downStations.contains(section.getUpStation()))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("구간 정보가 올바르지 않습니다."));
	}

	private boolean isEmpty() {
		return sections.size() == 0;
	}
}
