package nextstep.subway.line.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

@Embeddable
@Access(AccessType.FIELD)
public class Distance {

    private static final String ERROR_MESSAGE_DISTANCE_BOUND = "거리는 0 이하가 될 수 없습니다.";
    private static final int MIN_DISTANCE = 1;

    private int distance;

    protected Distance() {

    }

    private Distance(int distance) {
        validateDistanceBound(distance);

        this.distance = distance;
    }

    public static Distance of(int distance) {
        return new Distance(distance);
    }

    public int getDistance() {
        return distance;
    }

    private void validateDistanceBound(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException(ERROR_MESSAGE_DISTANCE_BOUND);
        }
    }

    public boolean isEqualOrMoreDistance(Distance distance) {
        return this.distance <= distance.distance;
    }
}
