package nextstep.subway.line.domain;

import nextstep.subway.line.exception.OutOfDistanceRangeException;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validDistance(distance);
        this.distance = distance;
    }

    private void validDistance(int distance) {
        if (distance <= 0) {
            throw new OutOfDistanceRangeException("거리는 0보다 커야 합니다.");
        }
    }

    public int minus(Distance distance) {
        return this.distance - distance.distance;
    }


}
