package nextstep.subway.station.exception;

public class StationNotFoundException extends RuntimeException {
	public StationNotFoundException(Long stationId) {
		super("역이 존재하지 않습니다. id: " + stationId);
	}

	public StationNotFoundException() {
		super("역이 존재하지 않습니다. id: ");
	}
}
