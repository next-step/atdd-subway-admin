package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static nextstep.subway.exception.CommonExceptionMessage.*;

@Embeddable
public class Sections {

	private static final int SECTION_MIN_SIZE = 1;

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public boolean add(final Section section) {
		if (this.sections.contains(section)) {
			return false;
		}
		if (!CollectionUtils.isEmpty(sections)) {
			validateStations(section);
			connectIfExistsUpStation(section);
			connectIfExistsDownStation(section);
		}
		return this.sections.add(section);
	}

	public List<Station> stationsBySorted() {
		return findFirstSection()
			.map(this::getSortedStations)
			.orElse(Collections.emptyList());
	}

	public void removeStation(final Station station) {
		checkPossibleRemoveByStation(station);
		Optional<Section> upSection = findSectionUsingFilter(value -> value.downStation().equals(station));
		Optional<Section> downSection = findSectionUsingFilter(value -> value.upStation().equals(station));
		if (!downSection.isPresent()) {
			removeSection(upSection.get());
			return;
		}
		if (!upSection.isPresent()) {
			removeSection(downSection.get());
			return;
		}
		disConnectSection(downSection.get(), upSection.get());
	}

	private void disConnectSection(final Section connectedUpStation, final Section connectedDownStation) {
		connectedUpStation.disconnectDownStation(connectedDownStation);
		this.sections.remove(connectedDownStation);
	}

	private void removeSection(final Section station) {
		this.sections.remove(station);
	}

	private void validateStations(final Section section) {
		boolean isExistsUpStation = containUpStation(section);
		boolean isExistsDownStation = containDownStation(section);
		if (isExistsUpStation && isExistsDownStation) {
			throw new IllegalArgumentException(EXISTS_ALL_STATIONS.message());
		}
		if (!isExistsUpStation && !isExistsDownStation) {
			throw new IllegalArgumentException(NOT_EXISTS_STATIONS.message());
		}
	}

	private void connectIfExistsUpStation(final Section section) {
		findSectionUsingFilter(value -> value.upStation().equals(section.upStation()))
			.ifPresent(value -> value.connectUpStationToDownStation(section));
	}

	private void connectIfExistsDownStation(final Section section) {
		findSectionUsingFilter(value -> value.downStation().equals(section.downStation()))
			.ifPresent(value -> value.connectDownStationToUpStation(section));
	}

	private Optional<Section> findSectionUsingFilter(Predicate<Section> filterToSections) {
		return this.sections.stream()
							.filter(filterToSections)
							.findFirst();
	}

	private boolean containUpStation(final Section section) {
		return containStationInSection(section.upStation());
	}

	private boolean containDownStation(final Section section) {
		return containStationInSection(section.downStation());
	}

	private boolean containStationInSection(final Station station) {
		return stations().contains(station);
	}

	private List<Station> stations() {
		return this.sections.stream()
							.flatMap(Section::streamOfStation)
							.distinct()
							.collect(Collectors.toList());
	}

	private Map<Station, Section> groupByUpStation() {
		return sections.stream()
					   .collect(Collectors.toMap(Section::upStation, Function.identity()));
	}

	private List<Station> downStations() {
		return sections.stream()
					   .map(Section::downStation)
					   .collect(Collectors.toList());
	}

	private Optional<Section> findFirstSection() {
		List<Station> downStations = downStations();
		return sections.stream()
					   .filter(section -> downStations.contains(section.upStation()) == false)
					   .findFirst();
	}

	private List<Station> getSortedStations(Section nextSection) {
		Map<Station, Section> upStationMap = groupByUpStation();
		List<Station> stations = new ArrayList<>();
		stations.add(nextSection.upStation());
		do {
			stations.add(nextSection.downStation());
			nextSection = upStationMap.get(nextSection.downStation());
		} while (nextSection != null);
		return stations;
	}

	private void checkPossibleRemoveByStation(final Station station) {
		if (!isPossibleRemoveSize()) { // Sections의 최소 갯수는 삭제할 수 없다.
			throw new IllegalArgumentException(CANNOT_DELETE_LAST_SECTION.message());
		}
		if (!containStationInSection(station)) { // 구간에 포함되어 있지 않으면 삭제 불가
			throw new IllegalArgumentException(NOT_EXISTS_STATIONS.message());
		}
	}

	private boolean isPossibleRemoveSize() {
		return this.sections.size() > SECTION_MIN_SIZE;
	}

}
