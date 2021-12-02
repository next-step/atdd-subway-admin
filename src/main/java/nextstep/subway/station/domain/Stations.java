package nextstep.subway.station.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Stations {
	@OneToMany(mappedBy = "line")
	private List<Station> stations = new ArrayList<>();

	public void add(Station station) {
		stations.add(station);
	}

	public boolean contains(Station station) {
		return stations.contains(station);
	}
}
