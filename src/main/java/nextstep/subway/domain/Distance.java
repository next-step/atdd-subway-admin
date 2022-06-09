package nextstep.subway.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    private static final long ZERO = 0;

    @JsonProperty("value")
    @Column(nullable = false)
    private long distance;

    protected Distance() {
    }

    public Distance(final long distance) {
        if (ZERO > distance) {
            throw new IllegalArgumentException("invalid parameter");
        }
        this.distance = distance;
    }

    public long getDistance() {
        return distance;
    }

    public Distance subtract(final Distance destination) {
        return destination.subtractBy(this.distance);
    }

    public Distance plus(Distance destination) {
        return destination.plus(this.distance);
    }

    private Distance plus(final long destination) {
        return new Distance(destination + this.distance);
    }

    private Distance subtractBy(final long source) {
        if ((source - this.distance) == ZERO) {
            throw new IllegalArgumentException("subtract result must is not zero");
        }
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
