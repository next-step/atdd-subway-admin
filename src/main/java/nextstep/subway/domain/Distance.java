package nextstep.subway.domain;

import nextstep.subway.exception.InvalidDistanceException;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    @Column(name = "distance")
    private int distance;

    public Distance() {
    }

    public Distance(int distance) {
        if (distance <= 0) {
            throw new InvalidDistanceException("구간 거리는 0보다 커야 합니다");
        }
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public Distance minusDistance(Distance distance) {
        int minusValue = this.distance - distance.getDistance();
        return new Distance(minusValue);
    }

    public Distance plusDistance(Distance distance) {
        int plusValue = this.distance + distance.getDistance();
        return new Distance(plusValue);
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

    @Override
    public String toString() {
        return "Distance{" +
                "distance=" + distance +
                '}';
    }
}
