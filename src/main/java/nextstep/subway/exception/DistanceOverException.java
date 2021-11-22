package nextstep.subway.exception;

public class DistanceOverException extends RuntimeException {
    private static final String DISTANCE_OVER_MESSAGE = "기존 역 사이 길이보다 크거나 같으면 등록 할 수 없습니다.";

    public DistanceOverException() {
        super(DISTANCE_OVER_MESSAGE);
    }
}
