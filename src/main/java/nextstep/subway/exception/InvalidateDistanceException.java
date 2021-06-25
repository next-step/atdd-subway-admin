package nextstep.subway.exception;

public class InvalidateDistanceException extends RuntimeException {

    private static final String INVALIDATE_DISTANCE_EXCEPTION_MESSAGE = "기존 구간의 길이보다 길 수 없습니다.";

    public InvalidateDistanceException() {
        super(INVALIDATE_DISTANCE_EXCEPTION_MESSAGE);
    }
}
