package nextstep.subway.station.exception;

import javax.persistence.EntityNotFoundException;

import nextstep.subway.common.ErrorCode;

public class StationNotFoundException extends EntityNotFoundException {
    private ErrorCode errorCode;

    public StationNotFoundException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public StationNotFoundException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
