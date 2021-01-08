package nextstep.subway.section.domain;

import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final int MIN_DISTANCE = 0;

    private int distance;

    protected Distance() {}

    public Distance(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException(MIN_DISTANCE + "미만의 거리는 존재할 수 없습니다");
        }
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isDistanceGreaterThanEqual(Distance distance) {
        return this.getDistance() >= distance.getDistance();
    }

    public void checkZeroDistance() {
        if (distance == MIN_DISTANCE) {
            throw new IllegalArgumentException(MIN_DISTANCE + "보다 큰 거리만 가능합니다");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Distance)) return false;
        Distance distance1 = (Distance) o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
