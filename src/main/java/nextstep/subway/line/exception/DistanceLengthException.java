package nextstep.subway.line.exception;

import nextstep.subway.excetpion.ErrorCode;

public class DistanceLengthException extends LineException {

    public DistanceLengthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DistanceLengthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
