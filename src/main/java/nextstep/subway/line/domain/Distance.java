package nextstep.subway.line.domain;

import nextstep.subway.exception.InputDataErrorCode;
import nextstep.subway.exception.InputDataErrorException;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    @Column
    private int distance;

    protected Distance() {
        distance = 0;
    }

    public Distance(int distance) {
        checkValidation(distance);
        this.distance = distance;
    }

    private void checkValidation(int distance) {
        if (distance <= 0) {
            throw new InputDataErrorException(InputDataErrorCode.DISTANCE_IS_NOT_LESS_THEN_ZERO);
        }
    }

    public Distance minus(Distance distance) {
        return new Distance(this.distance -= distance.distance);
    }

    public int getDistance() {
        return this.distance;
    }

    public boolean isSameDistance(Distance distance) {
        return this.equals(distance);
    }

    public boolean isOverDistance(Distance distance) {
        return this.distance > distance.getDistance();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Distance)) return false;
        Distance distance1 = (Distance) o;
        return getDistance() == distance1.getDistance();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDistance());
    }
}
