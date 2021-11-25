package nextstep.subway.line.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    protected static final int MIN_DISTANCE = 0;

    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if (distance <= MIN_DISTANCE) {
            throw new IllegalArgumentException("지하철 구간 사이의 거리는 " + MIN_DISTANCE + "보다 커야 합니다.");
        }
    }

    public int getDistance() {
        return distance;
    }
}
