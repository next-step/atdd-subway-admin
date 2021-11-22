package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	@OneToMany(
		cascade = CascadeType.ALL,
		fetch = FetchType.LAZY,
		mappedBy = "line",
		orphanRemoval = true)
	private List<Section> values = new ArrayList<>();

	public void add(Section section) {
		if (!isEmpty()) {
			throwOnIllegalSection(section);

			values.stream()
				.filter(s -> s.isOverlapped(section))
				.findFirst()
				.ifPresent(s -> s.divideBy(section));
		}

		values.add(section);
	}

	protected Sections() {

	}

	private Sections(List<Section> sections) {
		this.values = sections;
	}

	public static Sections of(List<Section> sections) {
		return new Sections(sections);
	}

	private void throwOnIllegalSection(Section section) {
		throwOnBothStationsNotRegistered(section);
		throwOnBothStationsAlreadyRegistered(section);
	}

	private void throwOnBothStationsNotRegistered(Section section) {
		List<Station> stations = getStationsInOrder();
		if (!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
			throw new SectionAddFailException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
		}
	}

	private void throwOnBothStationsAlreadyRegistered(Section section) {
		List<Station> stations = getStationsInOrder();
		if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())) {
			throw new SectionAddFailException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
		}
	}

	public List<Integer> getDistancesInOrder() {
		return getSectionsInOrder().stream()
			.map(Section::getDistance)
			.collect(Collectors.toList());
	}

	private List<Section> getSectionsInOrder() {
		if (isEmpty()) {
			return Collections.emptyList();
		}

		List<Section> sections = new ArrayList<>();
		Map<Station, Section> sectionByUpStation = getSectionByUpStation();
		List<Station> stationsInOrder = getStationsInOrder();
		for (int i = 0; i < stationsInOrder.size() - 1; i++) {
			Station station = stationsInOrder.get(i);
			sections.add(sectionByUpStation.get(station));
		}

		return sections;
	}

	private Map<Station, Section> getSectionByUpStation() {
		return values.stream()
			.collect(Collectors.toMap(Section::getUpStation, (section) -> section));
	}

	public List<Station> getStationsInOrder() {
		if (isEmpty()) {
			return Collections.emptyList();
		}

		List<Station> stations = new ArrayList<>();
		Map<Station, Station> downStationByUpStation = getDownStationByUpStation();
		Station upStation = getHeadUpStation();
		addStationsSequentially(stations, downStationByUpStation, upStation);

		return stations;
	}

	private void addStationsSequentially(
		List<Station> stations,
		Map<Station, Station> downStationByUpStation,
		Station upStation
	) {
		while (upStation != null) {
			stations.add(upStation);
			upStation = downStationByUpStation.get(upStation);
		}
	}

	private boolean isEmpty() {
		return values == null || values.isEmpty();
	}

	private Map<Station, Station> getDownStationByUpStation() {
		return values.stream()
			.collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
	}

	private Station getHeadUpStation() {
		List<Station> downStations = values.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());

		return values.stream()
			.map(Section::getUpStation)
			.filter(upStation -> !downStations.contains(upStation))
			.findFirst()
			.orElseThrow(IllegalStateException::new);
	}

	public int size() {
		return values.size();
	}
}
