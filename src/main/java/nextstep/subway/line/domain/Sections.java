package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

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
		if (sections == null) {
			return new Sections(new ArrayList<>());
		}

		return new Sections(sections);
	}

	private void throwOnIllegalSection(Section section) {
		throwOnBothStationsNotRegistered(section);
		throwOnBothStationsAlreadyRegistered(section);
	}

	private void throwOnBothStationsNotRegistered(Section section) {
		Stations stations = getStationsInOrder();
		if (!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
			throw new SectionAddFailException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
		}
	}

	private void throwOnBothStationsAlreadyRegistered(Section section) {
		Stations stations = getStationsInOrder();
		if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())) {
			throw new SectionAddFailException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
		}
	}

	public List<Section> getValues() {
		return values;
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
		Stations stationsInOrder = getStationsInOrder();
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

	public Stations getStationsInOrder() {
		if (isEmpty()) {
			return Stations.empty();
		}

		List<Station> stations = new ArrayList<>();
		Map<Station, Station> downStationByUpStation = getDownStationByUpStation();
		Station upStation = getHeadStation();
		addStationsSequentially(stations, downStationByUpStation, upStation);

		return Stations.of(stations);
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

	private Station getHeadStation() {
		List<Long> downStationIds = values.stream()
			.map(Section::getDownStation)
			.map(Station::getId)
			.collect(Collectors.toList());

		return values.stream()
			.map(Section::getUpStation)
			.filter(upStation -> !downStationIds.contains(upStation.getId()))
			.findFirst()
			.orElseThrow(IllegalStateException::new);
	}

	public int size() {
		return values.size();
	}

	public void removeByStation(Station station) {
		Stations stationsInOrder = getStationsInOrder();
		if (station.isHead(stationsInOrder) || station.isTail(stationsInOrder)) {
			remove(findOne(section -> section.containsAnyStation(station)));
			return;
		}

		Section first = findOne(section -> section.containsDownStation(station));
		Section second = findOne(section -> section.containsUpStation(station));
		first.merge(second, this);
	}

	private Section findOne(Predicate<Section> predicate) {
		return values.stream()
			.filter(predicate)
			.findFirst()
			.orElseThrow(IllegalStateException::new);
	}

	public void remove(Section section) {
		values.remove(section);
	}
}
