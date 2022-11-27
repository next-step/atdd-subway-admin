package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    private static final int MIN_VALUE = 1;

    @Column(nullable = false)
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validate(distance);
        this.distance = distance;
    }

    private void validate(int distance) {
        if (distance < MIN_VALUE) {
            throw new IllegalArgumentException("구간거리는 " + MIN_VALUE + "보다 크거나 같아야합니다.");
        }
    }

    public Distance add(int distance) {
        return new Distance(this.distance + distance);
    }

    public Distance subtract(int distance) {
        return new Distance(this.distance - distance);
    }

    public int value() {
        return distance;
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
