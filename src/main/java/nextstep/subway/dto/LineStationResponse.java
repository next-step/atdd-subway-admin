package nextstep.subway.dto;

import nextstep.subway.domain.LineStation;

public class LineStationResponse {

	private StationResponse upStation;
	private StationResponse downStation;
	private Integer distance;

	private LineStationResponse() {
	}

	public LineStationResponse(StationResponse upStation, StationResponse downStation, Integer distance) {
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}


	public StationResponse getUpStation() {
		return upStation;
	}

	public StationResponse getDownStation() {
		return downStation;
	}

	public Integer getDistance() {
		return distance;
	}

	public static LineStationResponse of(LineStation lineStation) {
		return new LineStationResponse(
				StationResponse.of(lineStation.getUpStation()),
				StationResponse.of(lineStation.getDownStation()),
				lineStation.getDistance());
	}
}
