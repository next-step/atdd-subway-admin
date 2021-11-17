package nextstep.subway.line.exception;

import nextstep.subway.excetpion.ErrorCode;

public class LineStationSafetyException extends LineException {

    public LineStationSafetyException(ErrorCode errorCode) {
        super(errorCode);
    }

    public LineStationSafetyException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
