package nextstep.subway.station.domain;

import java.util.ArrayList;
import java.util.List;

public class Stations {
	private final List<Station> values;

	private Stations(List<Station> stations) {
		this.values = stations;
	}

	public static Stations of(List<Station> stations) {
		if (stations == null) {
			return new Stations(new ArrayList<>());
		}

		return new Stations(stations);
	}

	public List<Station> getValues() {
		return values;
	}

	public static Stations empty() {
		return Stations.of(new ArrayList<>());
	}

	public boolean contains(Station station) {
		return values.contains(station);
	}

	public int size() {
		return values.size();
	}

	public Station get(int index) {
		return values.get(index);
	}

	public boolean isEmpty() {
		return values == null || values.isEmpty();
	}

	public Station getHead() {
		if (isEmpty()) {
			return null;
		}

		return values.get(0);
	}

	public Station getTail() {
		if (isEmpty()) {
			return null;
		}

		return values.get(values.size() - 1);
	}
}
