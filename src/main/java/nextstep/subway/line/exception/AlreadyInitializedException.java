package nextstep.subway.line.exception;

public class AlreadyInitializedException extends RuntimeException {
    public AlreadyInitializedException(String message) {
        super(message);
    }
}
