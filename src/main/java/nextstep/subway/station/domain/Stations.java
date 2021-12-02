package nextstep.subway.station.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Stations {
	private List<Station> stations = new ArrayList<>();

	public void add(Station station) {
		stations.add(station);
	}

	public boolean contains(Station station) {
		return stations.contains(station);
	}

	public List<Station> get() {
		return Collections.unmodifiableList(stations);
	}
}
