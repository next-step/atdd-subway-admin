package nextstep.subway.station.domain;

import nextstep.subway.common.exception.ConflictException;

public class StationExistsAlreadyException extends ConflictException {

	public StationExistsAlreadyException(Station station) {
		super("Station이 이미 존재합니다. " + station.getId());
	}
}
