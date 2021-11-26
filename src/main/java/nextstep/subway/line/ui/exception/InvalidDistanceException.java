package nextstep.subway.line.ui.exception;

public class InvalidDistanceException extends RuntimeException {
    public InvalidDistanceException() {
        super();
    }

    public InvalidDistanceException(String message) {
        super(message);
    }

    public static InvalidDistanceException error(String message) {
        return new InvalidDistanceException(message);
    }
}
