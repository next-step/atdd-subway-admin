package nextstep.subway.line.domain;

import nextstep.subway.line.exception.IllegalDistanceException;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    private static final int MIN_DISTANCE = 0;

    @Column
    private int distance;

    protected Distance() {
    }

    private Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    public static Distance of(int distance) {
        return new Distance(distance);
    }

    private void validateDistance(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalDistanceException();
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
        return Objects.hashCode(distance);
    }

    @Override
    public String toString() {
        return String.valueOf(distance);
    }

    public int getDistance() {
        return this.distance;
    }

    public boolean isLessThanOrEqualTo(int distance) {
        return this.distance <= distance;
    }

    public Distance minus(int distance) {
        return Distance.of(this.distance - distance);
    }

    public boolean match(int distance) {
        return this.distance == distance;
    }
}
