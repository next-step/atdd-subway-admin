package nextstep.subway.line.application;

public class NoLineException extends RuntimeException {
    public NoLineException(String message) {
        super(message);
    }
}
