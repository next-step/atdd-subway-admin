package nextstep.subway.line.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column(name = "distance", nullable = false)
    private int value;

    protected Distance() {
        this(0);
    }

    public Distance(int value) {
        validate(value);
        this.value = value;
    }

    private void validate(int distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("거리는 음수가 될 수 없습니다.");
        }
    }

    public int value() {
        return value;
    }

    public Distance add(Distance distance) {
        return new Distance(value + distance.value);
    }

    public Distance minus(Distance distance) {
        return new Distance(value - distance.value);
    }

    public boolean isGreaterThan(Distance distance) {
        return value > distance.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
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
