package nextstep.subway.domain;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {

    private int distance;

    public Distance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_DISTANCE_VALUE.getMessage());
        }
        this.distance = distance;
    }

    public Distance(int distance, boolean isZeroAllowed) {
        if (isZeroAllowed && distance < 0) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_DISTANCE_VALUE.getMessage());
        }
        if (!isZeroAllowed && distance <= 0) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_DISTANCE_VALUE.getMessage());
        }
        this.distance = distance;
    }

    protected Distance() {

    }

    public int getDistance() {
        return distance;
    }

    public Distance compareTo(Distance distance) {
        int newDistance = this.distance - distance.getDistance();
        if (newDistance <= 0) {
            throw new IllegalArgumentException(ErrorMessage.EXCEED_SECTION_DISTANCE.getMessage());
        }
        return new Distance(newDistance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance1 = (Distance) o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
