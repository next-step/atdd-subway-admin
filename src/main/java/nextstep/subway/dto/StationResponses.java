package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.List;

import nextstep.subway.domain.Station;

public class StationResponses {
	private final List<StationResponse> stationResponses;

	public StationResponses(Station... stations) {
		this.stationResponses = new ArrayList<StationResponse>();

		for (Station station : stations) {
			this.stationResponses.add(StationResponse.of(station));
		}
	}

	public List<StationResponse> getStationResponses() {
		return stationResponses;
	}
}
