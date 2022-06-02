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
            throw new InvalidDistanceException("구간 거리는 0보다 커야 합니다");
        }
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }
}
