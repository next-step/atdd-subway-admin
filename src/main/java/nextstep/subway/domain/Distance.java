package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import nextstep.subway.exception.CannotRegisterException;
import nextstep.subway.exception.ExceptionType;

@Embeddable
public class Distance {

    @Column(name = "distance")
    private Long value;

    protected Distance() {

    }

    public Distance(Long value) {
        validateDistance(value);
        this.value = value;
    }

    private void validateDistance(Long value) {
        if (value <= 0) {
            throw new IllegalArgumentException(ExceptionType.MUST_BE_AT_LEAST_LENGTH_ONE.getMessage());
        }
    }

    public void minus(Distance target) {
        validateDistanceValue(target);
        this.value = value - target.getValue();
    }

    private void validateDistanceValue(Distance target) {
        if (target.getValue() >= value) {
            throw new CannotRegisterException(ExceptionType.IS_NOT_OVER_ORIGIN_DISTANCE);
        }
    }

    public Long getValue() {
        return value;
    }
}
