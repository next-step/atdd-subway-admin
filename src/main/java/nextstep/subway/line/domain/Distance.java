package nextstep.subway.line.domain;

import nextstep.subway.Exception.CannotUpdateSectionException;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("역 간 거리는 0보다 커야합니다.");
        }
        this.distance = distance;
    }

    public void validateLargerThan(int distance) {
        if (this.distance <= distance) {
            throw new CannotUpdateSectionException("신규 역 간 거리가 기존 역 간 거리와 같거나 더 클 수 없습니다.");
        }
    }

    public Distance minus(int distance) {
        return new Distance(this.distance - distance);
    }

    public Distance plus(int otherSectionDistance) {
        return new Distance(this.distance + otherSectionDistance);
    }

    public int getDistance() {
        return distance;
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
}
