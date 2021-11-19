package nextstep.subway.line.exception;

public class NotFoundLineByIdException extends IllegalArgumentException {
    private static final String DEFAULT_MESSAGE = "해당 노선의 아이디가 존재하지 않습니다.";

    public NotFoundLineByIdException() {
        super(DEFAULT_MESSAGE);
    }
}
