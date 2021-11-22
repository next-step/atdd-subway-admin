package nextstep.subway.line.exception;

public class NotFoundLineException extends RuntimeException {
    public NotFoundLineException(String message) {
        super(message);
    }
}
