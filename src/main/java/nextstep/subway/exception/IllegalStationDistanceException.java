package nextstep.subway.exception;

public class IllegalStationDistanceException extends BusinessException {

    private static final String ERROR_MESSAGE = "새로운 역 사이의 거리는 기존 역 사이의 거리보다 크거나 같을 수 없습니다.";

    public IllegalStationDistanceException() {
        super(ERROR_MESSAGE);
    }
}
