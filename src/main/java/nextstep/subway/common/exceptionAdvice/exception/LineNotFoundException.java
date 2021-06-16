package nextstep.subway.common.exceptionAdvice.exception;

public class LineNotFoundException extends RuntimeException {
    private static final String MESSAGE = "%d 라인은 존재하지 않는 라인 입니다.";

    public LineNotFoundException(Long id) {
        super(String.format(MESSAGE, id));
    }
}
