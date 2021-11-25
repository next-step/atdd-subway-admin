package nextstep.subway.section.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    @Column
    private int value;

    protected Distance() {
    }

    private Distance(int value) {
        validate(value);
        this.value = value;
    }

    public static Distance from(int value) {
        return new Distance(value);
    }

    private void validate(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("거리는 0보다 커야 합니다.");
        }
    }

    public Distance subtract(Distance distance) {
        return new Distance(this.value - distance.value);
    }

    public boolean lessThanOrEqual(Distance other) {
        return this.value <= other.value;
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
        return value == distance1.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
