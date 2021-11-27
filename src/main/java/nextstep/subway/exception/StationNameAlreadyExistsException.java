package nextstep.subway.exception;

import nextstep.subway.enums.ErrorCode;

public class StationNameAlreadyExistsException extends SubwayRuntimeException {
    public StationNameAlreadyExistsException() {
        super(ErrorCode.STATION_NAME_ALREADY_EXISTS);
    }

    public StationNameAlreadyExistsException(final Throwable cause) {
        super(ErrorCode.STATION_NAME_ALREADY_EXISTS, cause);
    }
}
