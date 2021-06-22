package nextstep.subway.station.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.Embeddable;

public class StationGroup {

	private List<Station> stations = new ArrayList<>();

	protected StationGroup() {
	}

	public StationGroup(List<Station> stations) {
		this.stations = stations.stream().collect(Collectors.toList());
	}

	public List<Station> stations() {
		return stations;
	}

	public int size() {
		return stations.size();
	}

	public boolean contains(Station station) {
		return stations.contains(station);
	}

	public void add(Station station) {
		if (!contains(station)) {
			stations.add(station);
		}
	}

	public void addStationGroup(StationGroup stationGroup) {
		stationGroup.stations.forEach(this::add);
	}

	public void remove(Station station) {
		stations.remove(station);
	}

	public void removeStationGroup(StationGroup stationGroup) {
		stationGroup.stations.forEach(this::remove);
	}

	public void clear() {
		stations.clear();
	}

	public boolean isEmpty() {
		return stations.isEmpty();
	}

	public int indexOf(Station station) {
		return stations.indexOf(station);
	}

	public void add(int index, Station station) {
		if (!contains(station)) {
			stations.add(index, station);
		}
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		StationGroup that = (StationGroup)object;
		return stations.containsAll(that.stations)
			&& that.stations.containsAll(stations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(stations);
	}
}
