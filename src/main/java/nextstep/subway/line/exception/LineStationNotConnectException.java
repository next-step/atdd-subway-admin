package nextstep.subway.line.exception;

import nextstep.subway.excetpion.ErrorCode;

public class LineStationNotConnectException extends RuntimeException{

    private ErrorCode errorCode;

    public LineStationNotConnectException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public LineStationNotConnectException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
