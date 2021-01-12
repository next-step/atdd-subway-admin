package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Section> sections = new ArrayList<>();

	public List<Station> getStations() {
		return sections.stream()
			.flatMap(Section::getStations)
			.distinct()
			.collect(Collectors.toList());
	}

	public long add(Line line, Station upStation, Station downStation, int distance) {
		List<Station> stations = getStations();
		throwExceptionIfNotValid(stations, upStation, downStation);
		long addStationId = 0L;
		if (stations.contains(upStation)) {
			addStationId = downStation.getId();
			findByUpStation(upStation).ifPresent(section ->
				section.update(downStation, section.getDownStation(), section.getDistance() - distance));
		}
		if (stations.contains(downStation)) {
			addStationId = upStation.getId();
			findByDownStation(downStation).ifPresent(section ->
				section.update(section.getUpStation(), upStation, section.getDistance() - distance));
		}
		sections.add(Section.of(line, upStation, downStation, distance));
		return addStationId;
	}

	private void throwExceptionIfNotValid(List<Station> stations, Station upStation, Station downStation) {
		if (upStation.equals(downStation)) {
			throw new IllegalArgumentException("upStation과 downStation은 동일할 수 없습니다.");
		}
		if (stations.contains(upStation) && stations.contains(downStation)) {
			throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
		}
		if (!stations.isEmpty() && !stations.contains(upStation) && !stations.contains(downStation)) {
			throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
		}
	}

	private Optional<Section> findByUpStation(Station upStation) {
		return sections.stream()
			.filter(section -> section.getUpStation().equals(upStation))
			.findAny();
	}

	private Optional<Section> findByDownStation(Station downStation) {
		return sections.stream()
			.filter(section -> section.getDownStation().equals(downStation))
			.findAny();
	}

	public void remove(Station station) {
		Section removeTarget = findByUpStation(station).orElseGet(() -> findByDownStation(station)
				.orElseThrow(() -> new IllegalArgumentException("line에 속해있지 않은 구간은 삭제할 수 없습니다.")));
		List<Station> lastStations = findLastStations();
		if (lastStations.containsAll(Arrays.asList(removeTarget.getUpStation(), removeTarget.getDownStation()))) {
			throw new IllegalArgumentException("마지막 구간은 삭제할 수 없습니다.");
		}
		if (!lastStations.contains(station)) {
			findByDownStation(station)
				.ifPresent(section -> section.update(
					section.getUpStation(),
					removeTarget.getDownStation(),
					section.getDistance() + removeTarget.getDistance()));
		}
		sections.remove(removeTarget);
	}

	private List<Station> findLastStations() {
		return sections.stream()
			.flatMap(Section::getStations)
			.collect(Collectors.groupingBy(Station::getId))
			.values()
			.stream()
			.filter(stations -> stations.size() == 1)
			.map(stations -> stations.get(0))
			.collect(Collectors.toList());
	}

	public int getDistance(Station upStation, Station downStation) {
		return sections.stream()
			.filter(section -> section.getUpStation().equals(upStation) && section.getDownStation().equals(downStation))
			.findAny()
			.map(Section::getDistance)
			.orElse(-1);
	}
}