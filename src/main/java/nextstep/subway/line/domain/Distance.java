package nextstep.subway.line.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import nextstep.subway.common.Messages;
import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.NotValidateException;

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

    public static Distance valueOf(Integer distance) {
        return new Distance(distance);
    }

    public Distance minus(Distance target) {
        Integer calcDistance = this.distance - target.distance;
        validateZero(calcDistance);
        return Distance.valueOf(calcDistance);
    }

    public Distance plus(Distance target) {
        return Distance.valueOf(this.distance + target.distance);
    }

    private void validatePositive(Integer number) {
        if (number < ZERO) {
            throw new NotValidateException(Messages.NOT_POSITIVE_NUMBER.getValues());
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
        Distance other = (Distance) o;
        return Objects.equals(distance, other.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }

    @Override
    public String toString() {
        return "Distance{" +
            "distance=" + distance +
            '}';
    }

}
