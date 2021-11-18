package nextstep.subway.line.domain;

import java.util.ArrayList;
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

	public boolean isEmpty() {
		return sections.size() == 0;
	}

	private void add(Section requestSection, Line line) {
		sections.add(requestSection);
		requestSection.assignLine(line);
	}

	public void addSection(Section requestSection, Line line) {
		if (isEmpty()) {
			add(requestSection, line);
			return;
		}

		validateInsertSection(requestSection);

		List<Section> orderedSections = getOrderedSections();
		Section firstSection = orderedSections.get(0);
		Section lastSection = orderedSections.get(orderedSections.size() - 1);

		Section insertSection = null;

		for (Section currentSection : orderedSections) {
			if (currentSection.getUpStation() == requestSection.getUpStation()) { // 상행선 맞닿음
				validateInsertableLengthBetween(currentSection, requestSection);

				insertSection = new Section(requestSection.getDownStation(),
					currentSection.getDownStation(), currentSection.getDistance() - requestSection.getDistance());
				currentSection.changeDownSection(requestSection.getDownStation());
				currentSection.changeDistance(requestSection.getDistance());
				break;
			}

			if (currentSection.getDownStation() == requestSection.getDownStation()) { // 하행선 맞닿음
				validateInsertableLengthBetween(currentSection, requestSection);

				insertSection = new Section(requestSection.getUpStation(),
					currentSection.getDownStation(), requestSection.getDistance());
				currentSection.changeDownSection(requestSection.getUpStation());
				currentSection.changeDistance(currentSection.getDistance() - requestSection.getDistance());
				break;
			}

			if (currentSection.getUpStation() == requestSection.getDownStation()) { // 새로운 상행 종점
				if (!currentSection.equals(firstSection)) {
					continue;
				}

				insertSection = new Section(requestSection.getUpStation(),
					requestSection.getDownStation(), requestSection.getDistance());
				break;
			}

			if (currentSection.getDownStation() == requestSection.getUpStation()) { // 새로운 상행 종점
				if (!currentSection.equals(lastSection)) {
					continue;
				}

				insertSection = new Section(requestSection.getUpStation(),
					requestSection.getDownStation(), requestSection.getDistance());
				break;
			}
		}
		add(insertSection, line);
	}

	private void validateInsertSection(Section requestSection) {
		Set<Station> stations = getStations();

		Boolean hasUpStation = false;
		Boolean hasDownStation = false;
		if (stations.contains(requestSection.getUpStation())) {
			hasUpStation = true;
		}
		if (stations.contains(requestSection.getDownStation())) {
			hasDownStation = true;
		}

		if (hasUpStation && hasDownStation) {
			throw new IllegalArgumentException("이미 있는 구간 입니다.");

		}

		if (!hasUpStation && !hasDownStation) {
			throw new IllegalArgumentException("해당 노선에 추가할 수 없는 구간 정보입니다.");
		}
	}

	private void validateInsertableLengthBetween(Section currentSection, Section requestSection) {
		if (currentSection.getDistance() <= requestSection.getDistance()) { // 기존 역 사이 길이보다 크거나 같으면 등록 불가 예외 발생
			throw new IllegalArgumentException("기존 역 사이 길이와 같거나 긴 구간은 등록이 불가합니다.");
		}
	}

	private Set<Station> getStations() {
		Set<Station> stations = new HashSet<>();
		for (Section section : sections) {
			stations.add(section.getUpStation());
			stations.add(section.getDownStation());
		}
		return stations;
	}

	public List<Station> getOrderedStatons() {
		List<Section> orderedSections = getOrderedSections();
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

	public List<Section> getOrderedSections() {
		Section startSection = findStartStation();
		return orderedSections(startSection);
	}

	private List<Section> orderedSections(Section startSection) {
		Map<Station, Section> upStationAndSectionRoute = getSectionRoute();
		List<Section> sections = new ArrayList<>();

		Section nextSection = startSection;
		while (nextSection != null) {
			sections.add(nextSection);
			Station curDownStation = nextSection.getDownStation();
			nextSection = upStationAndSectionRoute.get(curDownStation);
		}
		return sections;
	}

	private Map<Station, Section> getSectionRoute() {
		return sections.stream()
			.collect(Collectors.toMap(Section::getUpStation, it -> it, (o1, o2) -> o1, HashMap::new));
	}

	private Section findStartStation() {
		// 전체 하행역 콜렉션 생성
		Set<Station> downStations = sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toSet());
		// 전체 상행역 중 하행역이 아닌 상행역 추출(=> 시작점)
		Optional<Section> startSection = sections.stream()
			.filter(it -> !downStations.contains(it.getUpStation()))
			.findFirst();
		return startSection.orElseThrow(() -> new IllegalStateException("구간 정보가 올바르지 않습니다."));
	}
}
