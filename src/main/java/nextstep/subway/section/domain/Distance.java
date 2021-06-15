package nextstep.subway.section.domain;

import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final int MINIMUM_DISTANCE = 1;
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    public static Distance copyOn(Section section) {
        return new Distance(section.getDistance().distance);
    }

    public Distance plusDistance(Distance distance) {
        this.distance += distance.distance;
        return this;
    }

    public Distance minusDistance(Distance distance) {
        validateResultDistance(distance);
        this.distance -= distance.distance;
        return this;
    }

    public boolean isGreaterThan(Distance distance) {
        return this.distance > distance.distance;
    }

    public boolean isEqualTo(Distance distance) {
        return isEqualTo(distance.distance);
    }

    public boolean isEqualTo(int distance) {
        return this.distance == distance;
    }

    public boolean isGreaterThanOrEqualTo(Distance distance) {
        return this.distance >= distance.distance;
    }

    public Distance plusDistance(Section section) {
        section.addDistanceTo(this);
        return this;
    }

    @Override
    public String toString() {
        return "Distance{" +
                "distance=" + distance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance1 = (Distance) o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }

    private void validateDistance(int distance) {
        if (distance < MINIMUM_DISTANCE) {
            throw new IllegalArgumentException("구간의 거리는 1 이상의 길이를 가져야 합니다.");
        }
    }

    private void validateResultDistance(Distance distance) {
        if (this.distance <= distance.distance) {
            throw new IllegalArgumentException("합산된 구간의 길이가 1보다 작습니다.");
        }
    }
}
