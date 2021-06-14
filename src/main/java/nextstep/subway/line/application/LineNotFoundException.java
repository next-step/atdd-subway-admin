package nextstep.subway.line.application;

public class LineNotFoundException extends RuntimeException {
    private static final String LINE_NOT_FOUND = "존재하지 않는 노선입니다. ";

    public LineNotFoundException() {
        super();
    }

    public LineNotFoundException(final String message) {
        super(message);
    }

    public LineNotFoundException(final Long id) {
        super(LINE_NOT_FOUND + id);
    }

    public LineNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public LineNotFoundException(final Throwable cause) {
        super(cause);
    }
}
