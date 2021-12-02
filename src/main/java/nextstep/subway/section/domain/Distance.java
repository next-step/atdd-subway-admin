package nextstep.subway.section.domain;

import nextstep.subway.section.exception.NotValidDistanceException;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final int MINIMUM_DISTANCE = 1;
    @Column(nullable = false)
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    public int getValue() {
        return distance;
    }

    public void plus(int distance) {
        validateDistance(distance);
        this.distance += distance;
    }

    private void validateDistance(int distance) {
        if (this.distance + distance < MINIMUM_DISTANCE) {
            throw new NotValidDistanceException("구간 입력이 잘못되었습니다.");
        }
    }
}
