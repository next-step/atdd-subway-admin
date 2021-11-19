package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.line.exception.DuplicatedSectionException;
import nextstep.subway.line.exception.IllegalSectionException;
import nextstep.subway.line.exception.SectionNotFoundException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
		CascadeType.REMOVE}, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public void add(Section section) {
		connect(section.getUpStation(), section.getDownStation(), section.getDistance());
		sections.add(section);
	}

	private void connect(Station upStation, Station downStation, int distance) {
		if (sections.isEmpty()) {
			return;
		}
		final List<Station> stations = getAllStationFrom(sections);
		final boolean hasSameUpStation = stations.contains(upStation);
		final boolean hasSameDownStation = stations.contains(downStation);
		validateToConnect(hasSameUpStation, hasSameDownStation);
		if (hasSameUpStation) {
			connectUpStationOfSectionToDownStationOf(upStation, downStation, distance);
		}
		if (hasSameDownStation) {
			connectDownStationOfSectionToUpStationOf(upStation, downStation, distance);
		}
	}

	private void validateToConnect(boolean hasSameUpStation, boolean hasSameDownStation) {
		if (hasSameUpStation && hasSameDownStation) {
			throw new DuplicatedSectionException();
		}
		if (!hasSameUpStation && !hasSameDownStation) {
			throw new IllegalSectionException();
		}
	}

	private void connectUpStationOfSectionToDownStationOf(Station upStation, Station downStation, int distance) {
		findAny(section -> section.equalsUpStation(upStation))
			.ifPresent(section -> section.updateUpStation(
				downStation, section.getDistance() - distance
			));
	}

	private void connectDownStationOfSectionToUpStationOf(Station upStation, Station downStation, int distance) {
		findAny(section -> section.equalsDownStation(downStation))
			.ifPresent(section -> section.updateDownStation(
				upStation, section.getDistance() - distance
			));
	}

	public Optional<Section> findAny(Predicate<? super Section> conditional) {
		return sections.stream().filter(conditional).findAny();
	}

	// @note: 순환선인 경우 별도의 종착 구간 정보 필요, but 순환선이 없다고 가정
	public Section getUpTerminalSection() {
		final Set<Station> downStations = sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toSet());
		return sections.stream()
			.filter(section -> !downStations.contains(section.getUpStation()))
			.findAny()
			.orElseThrow(SectionNotFoundException::new);
	}

	public List<Station> getAllStationSortedByUpToDownFrom(Section firstSection) {
		if (null == firstSection) {
			throw new SectionNotFoundException();
		}
		final List<Section> sections = sortSectionsByUpToDownFrom(firstSection);
		return getAllStationFrom(sections);
	}

	private List<Section> sortSectionsByUpToDownFrom(Section firstSection) {
		// @note: 동일한 upStation을 갖는 구간은 없다고 가정
		final Map<Station, Section> sectionByUpStation = toSectionMap(Section::getUpStation);
		final List<Section> sections = new LinkedList<>();
		Section section = firstSection;
		do {
			sections.add(section);
			section = sectionByUpStation.get(section.getDownStation());
		} while (null != section);
		return sections;
	}

	private <K> Map<K, Section> toSectionMap(Function<? super Section, ? extends K> keyMapper) {
		return sections.stream()
			.collect(Collectors.toMap(keyMapper, section -> section));
	}

	private List<Station> getAllStationFrom(List<Section> sections) {
		final List<Station> stations = new LinkedList<>();
		if (!sections.isEmpty() && null != sections.get(0)) {
			stations.add(sections.get(0).getUpStation());
		}
		stations.addAll(sections.stream().map(Section::getDownStation).collect(Collectors.toList()));
		return stations;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Sections)) {
			return false;
		}
		Sections that = (Sections)o;
		return Objects.equals(sections, that.sections);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sections);
	}
}
