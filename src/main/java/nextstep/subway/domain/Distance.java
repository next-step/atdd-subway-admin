package nextstep.subway.domain;

import nextstep.subway.exception.InvalidDistanceException;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column(name = "distance")
    private int distance;

    public Distance() {
    }

    public Distance(int distance) {
        if (distance <= 0) {
            throw new InvalidDistanceException();
        }
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }
}
