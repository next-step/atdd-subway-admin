package nextstep.subway.exception;

import nextstep.subway.domain.Distance;

public class InvalidDistanceException extends IllegalArgumentException {
    private static final String INVALID_ERROR = "노선 사이에 역을 추가할 경우 거리(%d)가 기존 거리보다 짧아야 합니다.";
    private static final String MIN_ERROR = "노선 사이에 역을 추가할 경우 거리(%d)가 기존 거리보다 짧아야 합니다.";

    public InvalidDistanceException(Distance distance) {
        super(String.format(INVALID_ERROR, distance.getDistance()));
    }

    public InvalidDistanceException(Long min) {
        super(String.format(MIN_ERROR, min));
    }
}
