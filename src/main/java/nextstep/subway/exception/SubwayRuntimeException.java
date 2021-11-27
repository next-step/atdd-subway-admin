package nextstep.subway.exception;

import nextstep.subway.enums.ErrorCode;

public class SubwayRuntimeException extends RuntimeException {
    public SubwayRuntimeException() {
        super(ErrorCode.DEFAULT.getMessage());
    }

    public SubwayRuntimeException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
    }

    public SubwayRuntimeException(final ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
    }

    public SubwayRuntimeException(Throwable cause) {
        super(cause);
    }

    public SubwayRuntimeException(final ErrorCode errorCode, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(errorCode.getMessage(), cause, enableSuppression, writableStackTrace);
    }
}
