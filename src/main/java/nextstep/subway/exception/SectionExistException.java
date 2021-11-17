package nextstep.subway.exception;

public class SectionExistException extends BusinessException {

    private static final String ERROR_MESSAGE = "이미 존재하는 구간입니다.";

    public SectionExistException() {
        super(ERROR_MESSAGE);
    }
}
