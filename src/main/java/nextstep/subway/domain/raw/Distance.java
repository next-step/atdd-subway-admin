package nextstep.subway.domain.raw;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import java.util.Objects;

import static nextstep.subway.constant.Message.NOT_VALID_UNDER_ZERO_DISTANCE;

@Embeddable
public class Distance {
    @Column
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {

        this.distance = distance;
    }

    public static Distance from(int distance) {
        validate(distance);
        return new Distance(distance);
    }

    private static void validate(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException(NOT_VALID_UNDER_ZERO_DISTANCE);
        }
    }

    public int getDistance() {
        return distance;
    }

    public Distance subtract(int distance) {
        return new Distance(this.distance - distance);
    }

    public Distance sumDistance(int distance) {
        return new Distance(this.distance + distance);
    }
}
