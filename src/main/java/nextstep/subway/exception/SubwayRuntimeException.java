package nextstep.subway.exception;

public class SubwayRuntimeException extends RuntimeException {
    public SubwayRuntimeException() {
    }

    public SubwayRuntimeException(String message) {
        super(message);
    }

    public SubwayRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubwayRuntimeException(Throwable cause) {
        super(cause);
    }

    public SubwayRuntimeException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
