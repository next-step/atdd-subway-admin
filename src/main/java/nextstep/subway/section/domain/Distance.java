package nextstep.subway.section.domain;

import nextstep.subway.section.exception.DistanceNegativeException;

import javax.persistence.Column;
import javax.persistence.Embeddable;

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
            throw new DistanceNegativeException();
        }
    }

    public int getDistance() {
        return distance;
    }

    public Distance subtract(int distance) {
        return new Distance(this.distance - distance);
    }
}
