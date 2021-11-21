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

	private static final String ERROR_MESSAGE_STATIONS_NOT_INCLUDED_IN_LINE = "기존에 존재하는 역을 포함하여 구간 추가를 요청해야 합니다.";
	private static final String ERROR_MESSAGE_ALREADY_EXIST_SECTION = "이미 등록된 구간 정보입니다.";

	private static final String ERROR_MESSAGE_HAVING_CIRCULATION_IN_LINE = "노선에 순환이 존재합니다.";

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	protected Sections() {
	}

	public void addSection(Section requestSection) {
		if (!isEmpty()) {
			reArrangeSectionsForSpace(requestSection);
		}
		sections.add(requestSection);
	}

	private void reArrangeSectionsForSpace(Section requestSection) {
		if (isOutBoundInsertable(requestSection)) {
			return;
		}

		adjustSectionBetweenStations(requestSection);
	}

	private boolean isOutBoundInsertable(Section requestSection) {
		List<Section> orderedSection = getOrderSections();
		Section startSection = orderedSection.get(0);
		Section endSection = orderedSection.get(orderedSection.size() - 1);

		if (startSection.matchUpStation(requestSection.getDownStation())) {
			return true;
		}

		if (endSection.matchDownStation(requestSection.getUpStation())) {
			return true;
		}
		return false;
	}

	private void adjustSectionBetweenStations(Section requestSection) {
		Optional<Section> findMatchUp = findMatchUpSection(requestSection);
		findMatchUp.ifPresent(matchSection -> matchSection.sliceUpSection(requestSection));

		Optional<Section> findMatchDown = findMatchDownSection(requestSection);
		findMatchDown.ifPresent(matchSection -> matchSection.sliceDownSection(requestSection));

		validateMatchOnlyOneStation(findMatchUp, findMatchDown);
	}

	private Optional<Section> findMatchUpSection(Section requestSection) {
		return sections.stream()
			.filter(section -> section.matchUpStation(requestSection.getUpStation()))
			.findAny();
	}

	private Optional<Section> findMatchDownSection(Section requestSection) {
		return sections.stream()
			.filter(section -> section.matchDownStation(requestSection.getDownStation()))
			.findAny();
	}

	private void validateMatchOnlyOneStation(Optional<Section> findMatchUp, Optional<Section> findMatchDown) {
		if (findMatchUp.isPresent() && findMatchDown.isPresent()) {
			throw new IllegalArgumentException(ERROR_MESSAGE_ALREADY_EXIST_SECTION);
		}

		if (!findMatchUp.isPresent() && !findMatchDown.isPresent()) {
			throw new IllegalArgumentException(ERROR_MESSAGE_STATIONS_NOT_INCLUDED_IN_LINE);
		}
	}

	public List<Section> getOrderSections() {
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

	private Section findStartSection() {
		Set<Station> downStations = sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toSet());
		// 전체 상행역 중 하행역이 아닌 상행역 추출(=> 시작점)
		return sections.stream()
			.filter(section -> !downStations.contains(section.getUpStation()))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException(ERROR_MESSAGE_HAVING_CIRCULATION_IN_LINE));
	}

	private Map<Station, Section> getSectionRoute() {
		return sections.stream()
			.collect(
				Collectors.toMap(Section::getUpStation,
					section -> section,
					(stationKey1, stationKey2) -> stationKey1,
					HashMap::new));
	}

	private boolean isEmpty() {
		return sections.size() == 0;
	}
}
