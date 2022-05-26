package nextstep.subway.section.domain;

import static nextstep.subway.section.domain.exception.StationExceptionMessage.*;

import java.util.Objects;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private int distance;

    protected Distance() {}

    private Distance(int distance) {
        this.distance = distance;
    }

    public static Distance from(int distance) {
        validateDistance(distance);
        return new Distance(distance);
    }

    public boolean isGraterThanOrEquals(Distance distance) {
        return this.distance >= distance.distance;
    }

    public void minus(Distance distance) {
        this.distance -= distance.distance;
    }

    private static void validateDistance(int distance) {
        if (distance < 1) {
            throw new IllegalArgumentException(DISTANCE_IS_MUST_BE_GREATER_THAN_1.getMessage());
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
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }

}
