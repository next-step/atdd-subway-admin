package nextstep.subway.domain.exception;

public class CannotAddSectionException extends RuntimeException{
    public CannotAddSectionException() {
    }

    public CannotAddSectionException(String message) {
        super(message);
    }

    public CannotAddSectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
