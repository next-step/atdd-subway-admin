package nextstep.subway.line.domain;

import javax.persistence.Embeddable;
import nextstep.subway.common.exception.IllegalDistanceException;

@Embeddable
public class Distance {

    private static final int MINIMUM_DISTANCE = 1;

    private int distance;

    protected Distance() {
    }

    private Distance(final int distance) {
        validateDistanceGreaterThanZero(distance);

        this.distance = distance;
    }

    public static Distance of(final int distance) {
        return new Distance(distance);
    }

    private void validateDistanceGreaterThanZero(int distance) {
        if (distance < MINIMUM_DISTANCE) {
            throw new IllegalDistanceException(MINIMUM_DISTANCE);
        }
    }
}
