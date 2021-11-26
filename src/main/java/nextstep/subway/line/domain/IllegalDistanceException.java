package nextstep.subway.line.domain;

public class IllegalDistanceException extends IllegalArgumentException {
    private static final String ILLEGAL_DISTANCE_ERROR = "역 사이의 거리는 %d보다 작을 수 없습니다.";

    public IllegalDistanceException(final int minimumDistance) {
        super(String.format(ILLEGAL_DISTANCE_ERROR, minimumDistance));
    }
}
