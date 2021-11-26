package nextstep.subway.line.ui.exception;

public class InvalidSectionException extends RuntimeException {
    public InvalidSectionException() {
        super();
    }

    public InvalidSectionException(String message) {
        super(message);
    }

    public static InvalidSectionException error(String message) {
        return new InvalidSectionException(message);
    }
}
