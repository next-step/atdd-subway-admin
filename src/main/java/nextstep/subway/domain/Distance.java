package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

@Embeddable
@Access(AccessType.FIELD)
public class Distance implements Comparable<Distance> {
    private static final int MIN = 0;

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

    private static void validate(int value) {
        if (value < MIN) {
            throw new IllegalArgumentException("Value can not be negative number");
        }
    }

    public Distance minus(Distance other) {
        return new Distance(value - other.value);
    }

    public Distance plus(Distance other) {
        return new Distance(value + other.value);
    }

    public boolean isGreaterThan(Distance other) {
        return value > other.value;
    }

    public boolean isLessThanOrEqual(Distance other) {
        return value <= other.value;
    }

    @Override
    public int compareTo(Distance o) {
        return Integer.compare(value, o.value);
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
    public int hashCode() {
        return Objects.hash(value);
    }
}
