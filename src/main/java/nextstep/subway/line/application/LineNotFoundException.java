package nextstep.subway.line.application;

public class LineNotFoundException extends RuntimeException {

    public LineNotFoundException() {
        super();
    }

    public LineNotFoundException(final String message) {
        super(message);
    }

    public LineNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public LineNotFoundException(final Throwable cause) {
        super(cause);
    }
}
