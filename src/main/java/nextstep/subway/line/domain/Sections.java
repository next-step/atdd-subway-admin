package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	List<Section> list = new ArrayList<>();

	public void add(Section candidate) {
		if (list.size() == 0) {
			list.add(candidate);

			return;
		}

		Section targetSection = getTargetSection(candidate);

		if (targetSection.isPossibleWithUpStationIntersected(candidate)) {
			addList(targetSection, candidate, candidate.getDownStation(), targetSection.getDownStation());

			return;
		}

		addList(targetSection, candidate, targetSection.getUpStation(), candidate.getUpStation());

		return;
	}

	private void addList(Section targetSection, Section candidate, Station upStation, Station downStation) {
		list.add(
			new Section(targetSection.getLine(), upStation, downStation,
				targetSection.getDistance() - candidate.getDistance()));
		list.add(candidate);
		list.remove(targetSection);
	}

	private Section getTargetSection(Section candidate) {
		return list.stream()
			.filter(
				element -> element.isPossibleWithUpStationIntersected(candidate)
					|| element.isPossilbeWithDownStationIntersected(candidate))
			.findFirst()
			.orElseThrow(() -> new NoSuchElementException("There is no such section"));
	}

	public List<Station> getOrderedStations() {
		Map<Station, Station> order = getStationOrder();
		return convertToOrderedList(order);
	}

	private Map<Station, Station> getStationOrder() {
		return list.stream().collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
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
