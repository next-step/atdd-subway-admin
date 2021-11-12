package nextstep.subway.exception;

public class LineNotFoundException extends BusinessException {

    private static final String ERROR_MESSAGE = "라인이 존재하지 않습니다.";

    public LineNotFoundException() {
        super(ERROR_MESSAGE);
    }
}
