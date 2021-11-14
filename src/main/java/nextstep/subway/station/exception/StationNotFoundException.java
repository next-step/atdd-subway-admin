package nextstep.subway.station.exception;

import nextstep.subway.excetpion.ErrorCode;

public class StationNotFoundException extends RuntimeException{

    private ErrorCode errorCode;

    public StationNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public StationNotFoundException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
