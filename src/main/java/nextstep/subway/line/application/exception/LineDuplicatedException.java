package nextstep.subway.line.application.exception;

public class LineDuplicatedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LineDuplicatedException(String message) {
        super(message);
    }
}