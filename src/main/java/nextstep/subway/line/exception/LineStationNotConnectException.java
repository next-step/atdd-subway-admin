package nextstep.subway.line.exception;

import nextstep.subway.excetpion.ErrorCode;

public class LineStationNotConnectException extends LineException {

    public LineStationNotConnectException(ErrorCode errorCode) {
        super(errorCode);
    }

    public LineStationNotConnectException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
