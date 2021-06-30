package nextstep.subway.section.domain;

import nextstep.subway.section.exception.BelowZeroDistanceException;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {

    @Column(name = "distance")
    private int value;

    protected Distance() {

    }

    public Distance(int value) {
        validateIllegalConstructor(value);
        this.value = value;
    }

    public void add(Distance distance) {
        validateIllegalDistance(distance);
        this.value += distance.value;
    }

    public void minus(Distance distance) {
        validateEnoughDistance(distance);
        this.value -= distance.value;
    }

    private void validateIllegalDistance(Distance distance) {
        if(distance.value <= 0) {
            throw new BelowZeroDistanceException();
        }
    }

    private void validateIllegalConstructor(int value) {
        if (value <= 0) {
            throw new BelowZeroDistanceException();
        }
    }

    private void validateEnoughDistance(Distance distance) {
        if (this.value - distance.value <= 0) {
            throw new BelowZeroDistanceException();
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
