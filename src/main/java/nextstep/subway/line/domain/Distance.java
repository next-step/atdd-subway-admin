package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {

    @Column
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간의 길이는 0보다 커야합니다. (입력값: " + distance + ")");
        }
        this.distance = distance;
    }

    public Distance minus(Distance distance) {
        return new Distance(this.distance - distance.distance);
    }

    public Distance plus(Distance distance) {
        return new Distance(this.distance + distance.distance);
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
