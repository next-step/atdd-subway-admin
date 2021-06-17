package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	public static final int SIZE_ZERO = 0;
	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	List<Section> sections = new ArrayList<>();

	public void add(Section candidate) {
		if (isSectionsEmpty()) {
			sections.add(candidate);

			return;
		}

		validateCandidate(candidate);

		Section commonUpStationSection = getCommonUpStationSection(candidate);
		if (isCommonStationExist(commonUpStationSection)) {
			addSectionWithCommonUpStation(candidate, commonUpStationSection);

			return;
		}

		Section commonDownStationSection = getCommonDownStationSection(candidate);
		if (isCommonStationExist(commonDownStationSection)) {
			addSectionWithCommonDownStation(candidate, commonDownStationSection);

			return;
		}

		sections.add(candidate);
	}

	private boolean isSectionsEmpty() {
		return sections.size() == SIZE_ZERO;
	}

	private boolean isCommonStationExist(Section commonUpStationSection) {
		return commonUpStationSection != null;
	}

	private void addSectionWithCommonDownStation(Section candidate, Section commonDownStationSection) {
		rearrangeSections(commonDownStationSection, candidate, commonDownStationSection.getUpStation(),
			candidate.getUpStation());
	}

	private Section getCommonDownStationSection(Section candidate) {
		return sections.stream()
			.filter(x -> x.hasSameDownStation(candidate))
			.findFirst().orElse(null);
	}

	private void addSectionWithCommonUpStation(Section candidate, Section commonUpStationSection) {
		rearrangeSections(commonUpStationSection, candidate, candidate.getDownStation(),
			commonUpStationSection.getDownStation());
	}

	private Section getCommonUpStationSection(Section candidate) {
		return sections.stream()
			.filter(x -> x.hasSameUpStation(candidate))
			.findFirst().orElse(null);
	}

	private void rearrangeSections(Section targetSection, Section candidate, Station upStation, Station downStation) {
		if (targetSection.getDistance() - candidate.getDistance() <= 0) {
			throw new IllegalArgumentException("The distance between new section must be less than target section");
		}

		sections.add(
			new Section(targetSection.getLine(), upStation, downStation,
				targetSection.getDistance() - candidate.getDistance()));
		sections.add(candidate);
		sections.remove(targetSection);
	}

	private void validateCandidate(Section candidate) {
		Set<Station> stations = getDistinceStations();
		validateAlreadyExistsTwoStations(candidate, stations);
		validateExistsConnectedStationToOldLine(candidate, stations);
	}

	private Set<Station> getDistinceStations() {
		return sections.stream()
			.map(x -> Arrays.asList(x.getUpStation(), x.getDownStation()))
			.flatMap(y -> y.stream())
			.collect(Collectors.toSet());
	}

	private void validateExistsConnectedStationToOldLine(Section candidate, Set<Station> stations) {
		if (!stations.contains(candidate.getUpStation()) && !stations.contains(candidate.getDownStation())) {
			throw new NoSuchElementException("There is no such section");
		}
	}

	private void validateAlreadyExistsTwoStations(Section candidate, Set<Station> stations) {
		if (stations.contains(candidate.getUpStation()) && stations.contains(candidate.getDownStation())) {
			throw new IllegalArgumentException("Each two stations are already in the line");
		}
	}

	public List<Station> getOrderedStations() {
		return convertToOrderedList(getStationMap());
	}

	private Map<Station, Station> getStationMap() {
		return sections.stream().collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
	}

	private List<Station> convertToOrderedList(Map<Station, Station> order) {
		List<Station> stations = new ArrayList<>();
		Station key = getStartStation(order);

		stations.add(key);

		while (order.containsKey(key)) {
			stations.add(order.get(key));
			key = order.get(key);
		}

		return stations;
	}

	private Station getStartStation(Map<Station, Station> order) {
		return order.keySet()
			.stream()
			.filter(x -> !order.containsValue(x))
			.findFirst()
			.orElseThrow(() -> new NoSuchElementException("There is no start point"));
	}
}
