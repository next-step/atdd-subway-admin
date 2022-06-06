package nextstep.subway.line.exception;

public class LineException extends RuntimeException {
    private final String code;

    public LineException(final String code, final String message) {
        super(message);
        this.code = code;
    }

    public LineException(final LineExceptionType type) {
        super(type.getMessage());
        this.code = type.getCode();
    }

    public String getCode() {
        return code;
    }
}
