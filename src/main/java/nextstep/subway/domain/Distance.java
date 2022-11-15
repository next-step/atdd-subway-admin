package nextstep.subway.domain;

import nextstep.subway.constant.ErrorCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final int ZERO = 0;

    @Column(nullable = false)
    private int distance;

    protected Distance() {}

    public Distance(int distance) {
        validate(distance);
        this.distance = distance;
    }

    private void validate(int distance) {
        if (distance <= ZERO) {
            throw new IllegalArgumentException(ErrorCode.DISTANCE_BIGGEST_THAN_ZERO.getMessage());
        }
    }

    public int get() {
        return distance;
    }

    public Distance subtract(Distance newDistance) {
        return new Distance(distance - newDistance.distance);
    }

    public Distance add(Distance another) {
        return new Distance(distance + another.distance);
    }
}
