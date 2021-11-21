package nextstep.subway.line.exception;

public class NotEmptyLineNameException extends IllegalArgumentException {
    public static final String DEFAULT_MESSAGE = "노선의 이름이 빈값일 수 없습니다.";

    public NotEmptyLineNameException() {
        super(DEFAULT_MESSAGE);
    }
}
