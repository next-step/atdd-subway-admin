package nextstep.subway.line.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import nextstep.subway.common.Messages;
import nextstep.subway.exception.BusinessException;

@Embeddable
public class Distance {
    public static final Integer ZERO = 0;

    @Column(nullable = false)
    private Integer distance;

    protected Distance() {
    }

    private Distance(Integer distance) {
        validatePositive(distance);
        this.distance = distance;
    }

    public static Distance createDownDistance() {
        return new Distance(ZERO);
    }

    public static Distance valueOf(Integer distance) {
        return new Distance(distance);
    }

    public Distance minus(Distance target) {
        Integer calcDistance = this.distance - target.distance;
        validateZero(calcDistance);
        return Distance.valueOf(calcDistance);
    }

    private void validatePositive(Integer number) {
        if (number < ZERO) {
            throw new BusinessException(Messages.NOT_POSITIVE_NUMBER.getValues());
        }
    }

    private void validateZero(Integer number) {
        if (number <= ZERO) {
            throw new BusinessException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distance distance1 = (Distance) o;
        return Objects.equals(distance, distance1.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }

}
