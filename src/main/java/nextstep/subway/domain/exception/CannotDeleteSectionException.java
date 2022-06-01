package nextstep.subway.domain.exception;

public class CannotDeleteSectionException extends RuntimeException {
    public CannotDeleteSectionException() {
    }

    public CannotDeleteSectionException(String message) {
        super(message);
    }

    public CannotDeleteSectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
