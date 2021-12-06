package nextstep.subway.section.exception;

public class IllegalDistanceError extends RuntimeException {
    public IllegalDistanceError() {
        super("구간 길이는 0보다 커야합니다 ");
    }
}
