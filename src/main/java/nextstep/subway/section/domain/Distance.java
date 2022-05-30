package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import nextstep.subway.global.exception.BadRequestException;
import nextstep.subway.global.exception.ExceptionType;

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
            throw new BadRequestException(ExceptionType.MUST_BE_AT_LEAST_LENGTH_ONE);
        }
    }

    public void minus(Distance target) {
        validateDistanceValue(target);
        this.value = value - target.getValue();
    }

    private void validateDistanceValue(Distance target) {
        if (target.getValue() >= value) {
            throw new BadRequestException(ExceptionType.IS_NOT_OVER_ORIGIN_DISTANCE);
        }
    }

    public Long getValue() {
        return value;
    }
}
