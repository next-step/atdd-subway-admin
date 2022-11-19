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
        validate(distance);
        this.distance = distance;
    }

    private void validate(int distance) {
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
}
