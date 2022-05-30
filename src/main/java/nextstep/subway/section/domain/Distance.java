package nextstep.subway.section.domain;

import java.util.Objects;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private static final int MINIMUM_DISTANCE = 1;
    private static final String DISTANCE_UNDER_MINIMUM_ERROR = "구간 거리는 1 보다 커야 합니다.";

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

    private void validateDistance(int distance) {
        if (distance < MINIMUM_DISTANCE) {
            throw new IllegalArgumentException(DISTANCE_UNDER_MINIMUM_ERROR);
        }
    }

    public boolean isBiggerAndEqual(Distance other) {
        return this.distance >= other.distance;
    }

    public void update(Distance newDistance) {
        this.distance = newDistance.distance;
    }

    public Distance minus(Distance other) {
        return Distance.from(this.distance - other.distance);
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
