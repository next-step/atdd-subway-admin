package nextstep.subway.exception;

public class NotFoundSectionException extends IllegalArgumentException {
    private static final String message = "노선에 일치하는 구간이 없습니다.";

    public NotFoundSectionException() {
        super(message);
    }
}
