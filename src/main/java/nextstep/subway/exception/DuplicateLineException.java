package nextstep.subway.exception;

public class DuplicateLineException extends BusinessException {

    private static final String ERROR_MESSAGE = "이미 존재하는 라인 입니다";

    public DuplicateLineException() {
        super(ERROR_MESSAGE);
    }
}
