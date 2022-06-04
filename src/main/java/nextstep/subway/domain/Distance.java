package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final long MIN_DISTANCE = 1;
    @Column
    private long distance;

    protected Distance() {
    }

    Distance(long distance) {
        invalidInputCheck(distance);
        this.distance = distance;
    }

    public Distance addDistance(Distance targetDistance) {
        return new Distance(this.distance + targetDistance.distance);
    }

    public Distance subtractDistance(Distance targetDistance) {
        validDistanceCheck(targetDistance);
        return new Distance(this.distance - targetDistance.distance);
    }

    private void invalidInputCheck(long distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException("구간은 최소 " + MIN_DISTANCE + " 이상의 값이어야 합니다.");
        }
    }

    private void validDistanceCheck(Distance newDistance) {
        if (this.distance <= newDistance.distance) {
            throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같으면 추가할 수 없음");
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
        Distance distance1 = (Distance) o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
