package nextstep.subway.section.domain;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    private static final String INVALID_DISTANCE = "잘못된 거리 입력입니다";

    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validate(distance);
        this.distance = distance;
    }

    public Distance minus(Distance distance) {
        return new Distance(this.distance - distance.getDistance());
    }

    public Distance plus(Distance distance) {
        return new Distance(this.distance + distance.getDistance());
    }

    private void validate(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException(INVALID_DISTANCE);
        }
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Distance)) return false;
        Distance distance1 = (Distance) o;
        return getDistance() == distance1.getDistance();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDistance());
    }

    @Override
    public String toString() {
        return "Distance{" +
                "distance=" + distance +
                '}';
    }
}
