package nextstep.subway.exception;

import nextstep.subway.enums.ErrorCode;

public class LineNameAlreadyExistsException extends SubwayRuntimeException {
    public LineNameAlreadyExistsException() {
        super(ErrorCode.LINE_NAME_ALREADY_EXISTS);
    }

    public LineNameAlreadyExistsException(final Throwable cause) {
        super(ErrorCode.LINE_NAME_ALREADY_EXISTS, cause);
    }
}
