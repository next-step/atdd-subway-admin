package nextstep.subway.line.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.util.Objects;

import static java.lang.Integer.compare;

@Embeddable
@Access(AccessType.FIELD)
public class Distance implements Comparable<Distance> {

    public static final int MINIMUM_DISTANCE = 0;
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validate(distance);
        this.distance = distance;
    }

    private static void validate(int distance) {
        if (distance <= MINIMUM_DISTANCE) {
            throw new IllegalArgumentException();
        }
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

    @Override
    public int compareTo(Distance o) {
        return compare(this.distance, o.distance);
    }

    public int getDistance() {
        return this.distance;
    }
}
