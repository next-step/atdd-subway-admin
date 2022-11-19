package nextstep.subway.domain;

import nextstep.subway.exception.CannotAddSectionException;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {

    private static final int ZERO_DISTANCE = 0;

    @Column(nullable = false)
    private Integer value;

    protected Distance() {
    }

    private Distance(Integer value) {
        this.value = value;
    }

    public static Distance valueOf(Integer value) {
        Objects.requireNonNull(value);

        verifyIsNotZero(value);
        return new Distance(value);
    }

    private static void verifyIsNotZero(int value) {
        if (value <= ZERO_DISTANCE) {
            throw new IllegalArgumentException("거리는 1보다 작을 수 없습니다.");
        }
    }

    public Integer getValue() {
        return value;
    }

    public boolean isLessThan(Distance otherDistance) {
        return this.value <= otherDistance.value;
    }

    public Distance subtract(Distance otherDistance) {
        verifyDistanceShorter(otherDistance);
        return Distance.valueOf(value - otherDistance.value);
    }

    public Distance add(Distance otherDistance) {
        return Distance.valueOf(value + otherDistance.value);
    }

    private void verifyDistanceShorter(Distance otherDistance) {
        if (isLessThan(otherDistance)) {
            throw new CannotAddSectionException(CannotAddSectionException.LONGER_THAN_SECTION);
        }
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
        return value.equals(distance.value);
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
