package nextstep.subway.exception;

public class NotFoundLineException extends BadRequestException {
    private static final String NOT_FOUND_LINE_MESSAGE = "노선 찾을 수 없습니다.";

    public NotFoundLineException() {
        super(NOT_FOUND_LINE_MESSAGE);
    }
}
