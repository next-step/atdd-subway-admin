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

import static nextstep.subway.exception.CommonExceptionMessage.EXISTS_ALL_STATIONS;
import static nextstep.subway.exception.CommonExceptionMessage.NOT_EXISTS_STATIONS;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public void add(final Section section) {
		if (!CollectionUtils.isEmpty(sections)) {
			validateStations(section);
			connectIfExistsUpStation(section);
			connectIfExistsDownStation(section);
		}
		this.sections.add(section);
	}

	public List<Station> stationsBySorted() {
		return findFirstSection()
			.map(this::getSortedStations)
			.orElse(Collections.emptyList());
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
		return stations().contains(section.upStation());
	}

	private boolean containDownStation(final Section section) {
		return stations().contains(section.downStation());
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

	public boolean contains(final Section section) {
		return this.sections.contains(section);
	}

}
