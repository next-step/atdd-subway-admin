package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final int ZERO = 0;
    private static final String INVALID_VALUE_MESSAGE = "거리는 0보다 커야 합니다.";

    @Column(nullable = false)
    private int distance;

    protected Distance() {}

    public Distance(int distance) {
        validate(distance);
        this.distance = distance;
    }

    private void validate(int distance) {
        if (distance <= ZERO) {
            throw new IllegalArgumentException(INVALID_VALUE_MESSAGE);
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
