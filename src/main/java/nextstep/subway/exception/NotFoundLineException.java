package nextstep.subway.exception;

public class NotFoundLineException extends RuntimeException {
    public NotFoundLineException(String message) {
        super(message);
    }
}
