package nextstep.subway.common.exception;

public class NotFoundResourceException extends RuntimeException {

    public NotFoundResourceException() {
    }

    public NotFoundResourceException(String message) {
        super(message);
    }

    public NotFoundResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundResourceException(Throwable cause) {
        super(cause);
    }

    public NotFoundResourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
