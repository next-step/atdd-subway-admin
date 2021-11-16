package nextstep.subway.line.exception;

import nextstep.subway.excetpion.ErrorCode;

public class DuplicateLineStationException extends RuntimeException {

    private ErrorCode errorCode;

    public DuplicateLineStationException() {
    }

    public DuplicateLineStationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
