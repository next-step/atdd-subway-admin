package nextstep.subway.line.exception;

public class NotFoundException extends RuntimeException {
    private static final String EXCEPTION_STATEMENT = "%s 을 찾을 수 없습니다.";

    public NotFoundException(String target) {
        super(String.format(EXCEPTION_STATEMENT, target));
    }
}
