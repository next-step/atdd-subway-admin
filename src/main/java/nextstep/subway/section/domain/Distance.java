package nextstep.subway.section.domain;

import nextstep.subway.common.exception.NegativeNumberException;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if (distance < 0) {
            throw new NegativeNumberException(distance);
        }
    }

    public int getDistance() {
        return distance;
    }
}
