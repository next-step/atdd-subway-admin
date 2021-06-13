package nextstep.subway.exception.line;

public class NotFoundLineException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String NO_LINE = "존재하지 않는 노선입니다.";

    public NotFoundLineException() {
        super(NO_LINE);
    }

    public NotFoundLineException(String message) {
        super(message);
    }
}
