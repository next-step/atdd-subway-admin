package nextstep.subway.line.exception;

import nextstep.subway.excetpion.ErrorCode;

public class LineException extends RuntimeException {

    private final ErrorCode errorCode;

    public LineException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public LineException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
