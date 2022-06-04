package nextstep.subway.exception;

import static nextstep.subway.constant.ExceptionMessages.DISTANCE_IS_EQUAL_OR_GREATER;

public class DistanceIsEqualOrGreaterException extends RuntimeException {
    public DistanceIsEqualOrGreaterException() {
        super(DISTANCE_IS_EQUAL_OR_GREATER);
    }
}
