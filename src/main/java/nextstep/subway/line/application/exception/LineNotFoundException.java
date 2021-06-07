package nextstep.subway.line.application.exception;

public class LineNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LineNotFoundException(String message) {
        super(message);
    }
}
