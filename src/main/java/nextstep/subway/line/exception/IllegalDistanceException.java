package nextstep.subway.line.exception;

public class IllegalDistanceException extends IllegalArgumentException {
    public static final String DEFAULT_MESSAGE = "역간의 거리는 0보다 커야 합니다.";

    public IllegalDistanceException() {
        super(DEFAULT_MESSAGE);
    }
}
