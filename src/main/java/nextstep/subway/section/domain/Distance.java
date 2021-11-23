package nextstep.subway.section.domain;

import java.util.Objects;
import javax.persistence.Embeddable;
import nextstep.subway.common.Messages;
import nextstep.subway.exception.BusinessException;

@Embeddable
public class Distance {
    public static final Integer ZERO = 0;

    private Integer distance;

    protected Distance() {
    }

    private Distance(Integer distance) {
        this.distance = distance;
    }

    public static Distance createDownDistance() {
        return new Distance(ZERO);
    }

    public static Distance valueOf(Integer distance) {
        return new Distance(distance);
    }

    public Integer minus(Distance target) {
        Integer calcDistance = this.distance - target.distance;
        validatePositive(calcDistance);
        return calcDistance;
    }

    private void validatePositive(Integer number) {
        if (number == ZERO) {
            throw new BusinessException(Messages.SAME_DISTANCE.getValues());
        }

        if (number < ZERO) {
            throw new BusinessException(Messages.LONG_DISTANCE.getValues());
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
