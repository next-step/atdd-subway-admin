package nextstep.subway.line.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    @Column(name = "distance", nullable = false)
    public int value;

    protected Distance() {
    }

    private Distance(int value) {
        validate(value);
        this.value = value;
    }

    public static Distance from(int value) {
        return new Distance(value);
    }

    public boolean moreThan(Distance distance) {
        return value >= distance.value;
    }

    public Distance subtract(Distance distance) {
        return from(value - distance.value);
    }

    private void validate(int value) {
        if (lessThanOrEqualZero(value)) {
            throw new IllegalArgumentException(
                String.format("distance value(%d) must be greater than zero", value));
        }
    }

    private boolean lessThanOrEqualZero(int value) {
        return value <= 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distance distance = (Distance) o;
        return value == distance.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
