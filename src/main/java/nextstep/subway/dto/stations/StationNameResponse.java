package nextstep.subway.dto.stations;

import nextstep.subway.domain.station.Station;

public class StationNameResponse {

	private Long id;

	private String name;

	private StationNameResponse() {
	}

	private StationNameResponse(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public static StationNameResponse of(Station station) {
		return new StationNameResponse(station.getId(), station.getName());
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
