package nextstep.subway.generic.domain.distance;

import java.util.Objects;

public class Distance implements Comparable<Distance> {
    private final int value;

    protected Distance(final int value) {
        validateDistance(value);
        this.value = value;
    }

    public static Distance valueOf(final int value) {
        return new Distance(value);
    }

    public int getValue() {
        return value;
    }

    private void validateDistance(final long value) {
        if (value < 0) {
            throw new IllegalArgumentException("거리값은 음수가 될수 없습니다.");
        }
    }

    @Override
    public int compareTo(final Distance o) {
        return Integer.compare(value, o.value);
    }

    public Distance minus(final Distance distance) {
        return valueOf(value - distance.value);
    }

    public boolean isGreaterThanOrEqualTo(final Distance distance) {
        return this.value >= distance.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Distance)) {
            return false;
        }
        final Distance distance = (Distance) o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Distance{" +
                "value=" + value +
                '}';
    }
}
