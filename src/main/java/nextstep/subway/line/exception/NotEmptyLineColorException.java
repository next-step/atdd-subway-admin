package nextstep.subway.line.exception;

public class NotEmptyLineColorException extends IllegalArgumentException {
    public static final String DEFAULT_MESSAGE = "노선의 색상값이 빈값일 수 없습니다.";

    public NotEmptyLineColorException() {
        super(DEFAULT_MESSAGE);
    }
}
