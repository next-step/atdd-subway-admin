package nextstep.subway.exception;

public class LineNotExistException extends BusinessException {

    private static final String ERROR_MESSAGE = "사용자가 존재하지 않습니다.";

    public LineNotExistException() {
        super(ERROR_MESSAGE);
    }
}
