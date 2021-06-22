package nextstep.subway.section.domain;

import nextstep.subway.common.ErrorMessage;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    public static final int DISTANCE_MIN_NUMBER = 1;

    @Column(name = "distance")
    private int value;

    protected Distance() {
    }

    public Distance(int value) {
        checkValid(value);
        this.value = value;
    }

    public Distance diff(Distance requestDistance) {
        int newValue = Math.abs(value - requestDistance.value);
        if (value < newValue) {
            throw new RuntimeException(ErrorMessage.DISTANCE_TOO_LONG);
        }
        return new Distance(newValue);
    }

    public Distance add(Distance requestDistance) {
        return new Distance(value + requestDistance.value);
    }

    private void checkValid(int value) {
        if (value <= DISTANCE_MIN_NUMBER) {
            throw new RuntimeException(ErrorMessage.DISTANCE_TOO_LONG);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance = (Distance) o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
