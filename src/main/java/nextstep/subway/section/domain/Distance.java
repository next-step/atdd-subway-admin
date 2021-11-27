package nextstep.subway.section.domain;

import nextstep.subway.common.exception.NegativeNumberDistanceException;

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
            throw new NegativeNumberDistanceException(distance);
        }
    }

    public int getDistance() {
        return distance;
    }

    public void changeDistance(int distance) {
        this.distance = distance;
    }

    public void subtractDistance(int distance) {
        validateDistance(this.distance - distance);
        this.distance -= distance;
    }
}
