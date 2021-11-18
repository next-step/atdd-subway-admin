package nextstep.subway.station.exception;

import nextstep.subway.common.ErrorCode;

public class StationException extends RuntimeException {

	public StationException(ErrorCode errorCode) {
		super(errorCode.getErrorMessage());
	}
}
