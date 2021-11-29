package nextstep.subway.section.domain;

import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final int MIN_DISTANCE = 1;
    private int distance;

    public Distance(int distance) {
        validate(distance);
        this.distance = distance;
    }

    protected Distance() {
    }

    private void validate(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException("1 이상의 길이만 입력 가능합니다. distance: " + distance);
        }
    }

    public Distance subtract(Distance distance) {
        return new Distance(this.distance - distance.distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Distance)) {
            return false;
        }
        Distance distance1 = (Distance)o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
