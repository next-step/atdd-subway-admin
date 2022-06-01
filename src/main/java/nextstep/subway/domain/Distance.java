package nextstep.subway.domain;

import static nextstep.subway.domain.ErrorMessage.DISTANCE_UNDER_ZERO;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final int MIN_DISTANCE = 0;

    @Column
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
            throw new IllegalArgumentException(DISTANCE_UNDER_ZERO.toString());
        }
    }
}
