package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	List<Section> list = new LinkedList<>();

	public void add(Section section) {
		list.add(section);
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
		Station key = null;

		for (Station station : order.keySet()) {
			if (!order.containsValue(station)) {
				key = station;
			}
		}
		return key;
	}
}
