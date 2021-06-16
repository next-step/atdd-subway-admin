package nextstep.subway.common;

import nextstep.subway.exception.SectionCreateFailException;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException();
        }
    }

    public void compareDistance(Distance compareDistance) {
        if (this.distance <= compareDistance.distance) {
            throw new SectionCreateFailException();
        }
    }

    public Distance minusDistance(Distance minusDistance) {
        return new Distance(this.distance - minusDistance.distance);
    }

    public int distance() {
        return distance;
    }
}
