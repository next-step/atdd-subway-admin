package nextstep.subway.line.domain;

import nextstep.subway.exception.BadRequestException;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {

    private int distance;

    protected Distance() {
    }

    private Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    public static Distance from(int distance) {
        return new Distance(distance);
    }

    public int value() {
        return distance;
    }

    public void minus(Distance distance) {
        this.distance = this.distance - distance.value();
    }

    public boolean isGreaterThan(Distance distance) {
        return this.distance > distance.value();
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new BadRequestException("구간의 거리는 1 이상으로 입력해주세요.");
        }
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
}
