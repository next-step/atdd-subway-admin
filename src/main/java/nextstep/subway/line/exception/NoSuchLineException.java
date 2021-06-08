package nextstep.subway.line.exception;

public class NoSuchLineException extends RuntimeException {
    public NoSuchLineException(String message) {
        super(message);
    }
}
