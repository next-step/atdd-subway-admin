package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    private static final Long ZERO = 0L;

    @Column(nullable = false)
    private Long distance;

    protected Distance() {
    }

    public Distance(final Long distance) {
        if (ZERO > distance) {
            throw new IllegalArgumentException("invalid parameter");
        }
        this.distance = distance;
    }

    public Long getDistance() {
        return distance;
    }

    public Distance subtract(final Distance destination) {
        return destination.subtractBy(this.distance);
    }

    public Distance plus(Distance destination) {
        return destination.plus(this.distance);
    }

    private Distance plus(final Long destination) {
        return new Distance(destination + this.distance);
    }

    private Distance subtractBy(final Long source) {
        return new Distance(source - this.distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance1 = (Distance) o;
        return Objects.equals(distance, distance1.distance);
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
