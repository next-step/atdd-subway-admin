package nextstep.subway.line.application.exception;

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
