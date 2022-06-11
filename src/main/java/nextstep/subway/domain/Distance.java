package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import nextstep.subway.exception.InvalidDistanceException;

@Embeddable
public class Distance {

    public static final int MIN_DISTANCE = 0;
    public static final String MIN_DISTANCE_MESSAGE = "구간 길이는 0 이하가 될 수 없습니다.";

    @Column(nullable = false)
    private int distance;

    protected Distance() {
    }

    private Distance(int distance) {
        validate(distance);
        this.distance = distance;
    }

    public static Distance from(int distance) {
        return new Distance(distance);
    }

    private void validate(int distance) {
        if (distance <= MIN_DISTANCE) {
            throw new InvalidDistanceException(MIN_DISTANCE_MESSAGE);
        }
    }

    public Distance subtract(Distance distance) {
        return new Distance(this.distance - distance.distance);
    }

    public Distance add(Distance distance) {
        return new Distance(this.distance + distance.distance);
    }

    public int value() {
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
