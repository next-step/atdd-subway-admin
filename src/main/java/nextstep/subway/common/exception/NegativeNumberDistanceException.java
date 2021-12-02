package nextstep.subway.common.exception;

public class NegativeNumberDistanceException extends RuntimeException {
    private static final String NEGATIVE_NUMBER_DISTANCE_MESSAGE = "현재 계산된 거리 값이 음수입니다. : ";
    private static final long serialVersionUID = 3L;

    public NegativeNumberDistanceException(int distance) {
        super(NEGATIVE_NUMBER_DISTANCE_MESSAGE + distance);
    }
}
