package nextstep.subway.line.exception;

public class NotFoundLineException extends RuntimeException {

    public static final String NOT_FOUND_LINE = "노선이 존재하지 않습니다.";

    public NotFoundLineException() {
        super(NOT_FOUND_LINE);
    }
}
