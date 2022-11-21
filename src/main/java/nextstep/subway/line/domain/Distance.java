package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    @Column(nullable = false)
    private int distance;

    public Distance() {
    }

    public Distance(int distance) {
        this.distance = distance;
    }

    public static Distance from(int distance) {
        return new Distance(distance);
    }

    public void modifyDistance(Distance otherDistance) {
        validateDistanceOver(otherDistance.getDistance());
        this.distance = this.distance - otherDistance.getDistance();
    }

    public Distance sum(Distance otherDistance) {
        return new Distance(this.distance + otherDistance.getDistance());
    }

    private void validateDistanceOver(int distance) {
        if (distance >= this.distance) {
            throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
        }
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distance distance1 = (Distance) o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
