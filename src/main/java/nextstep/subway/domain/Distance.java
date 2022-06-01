package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final int MIN_DISTANCE = 0;
    private static final IllegalArgumentException DISTANCE_VALID_EXCEPTION
            = new IllegalArgumentException("거리는 0보다 커야 합니다.");

    @Column
    private int distance;

    protected Distance() {

    }

    private Distance(int distance) {
        valid(distance);
        this.distance = distance;
    }

    public static Distance of(int distance) {
        return new Distance(distance);
    }

    private void valid(int distance) {
        if (MIN_DISTANCE >= distance) {
            throw DISTANCE_VALID_EXCEPTION;
        }
    }

    public int value() {
        return distance;
    }
}
