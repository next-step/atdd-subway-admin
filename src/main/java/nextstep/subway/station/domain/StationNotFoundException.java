package nextstep.subway.station.domain;

import nextstep.subway.common.exception.NotFoundException;

public class StationNotFoundException extends NotFoundException {

	public StationNotFoundException(Long stationId) {
		super("Station이 없습니다. " + stationId);
	}

	public StationNotFoundException(Long stationId, Throwable cause) {
		super("Station이 없습니다. " + stationId, cause);
	}
}
