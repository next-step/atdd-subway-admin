package nextstep.subway.line.exception;

import nextstep.subway.excetpion.ErrorCode;

public class DistanceLengthException extends RuntimeException {

    private ErrorCode errorCode;

    public DistanceLengthException() {
    }

    public DistanceLengthException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
