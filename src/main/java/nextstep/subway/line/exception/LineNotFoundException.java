package nextstep.subway.line.exception;

import nextstep.subway.excetpion.ErrorCode;

public class LineNotFoundException extends LineException {

    public LineNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public LineNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
