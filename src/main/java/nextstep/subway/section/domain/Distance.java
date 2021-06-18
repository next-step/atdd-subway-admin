package nextstep.subway.section.domain;

import nextstep.subway.common.ErrorMessage;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column(name = "distance")
    private int value;

    protected Distance() {
    }

    public Distance(int value) {
        checkNegative(value);
        this.value = value;
    }

    public Distance calculateDistance(Distance requestDistance) {
        return calculate(requestDistance);
    }

    private Distance calculate(Distance requestDistance) {
        return new Distance(value - requestDistance.value);
    }

    private void checkNegative(int value) {
        if (value < 0) {
            throw new RuntimeException(ErrorMessage.DISTANCE_TOO_LONG);
        }
    }
}
