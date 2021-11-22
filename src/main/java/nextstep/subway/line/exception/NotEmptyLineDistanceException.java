package nextstep.subway.line.exception;

public class NotEmptyLineDistanceException extends IllegalArgumentException {
    public static final String DEFAULT_MESSAGE = "구간의 거리가 빈값일 수 없습니다.";

    public NotEmptyLineDistanceException() {
        super(DEFAULT_MESSAGE);
    }
}
