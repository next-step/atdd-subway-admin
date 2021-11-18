package nextstep.subway.line.exception;

import nextstep.subway.excetpion.ErrorCode;

public class DuplicateLineStationException extends LineException {

    public DuplicateLineStationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DuplicateLineStationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
