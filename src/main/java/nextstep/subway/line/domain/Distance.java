package nextstep.subway.line.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private int distance;

    public Distance(int distance) {
        validateRanged(distance);
        this.distance = distance;
    }

    protected Distance() {
    }

    private void validateRanged(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("거리는 0보다 커야합니다.");
        }
    }

    public void calculateMinus(Distance distance) {
        validateLongerAndEqualsThan(distance);
        this.distance -= distance.distance();
    }

    private void validateLongerAndEqualsThan(Distance target) {
        if (this.distance <= target.distance()) {
            throw new IllegalArgumentException(String.format("거리가 %dm보다 짧아야합니다.", this.distance));
        }
    }

    public int distance() {
        return this.distance;
    }

    public void calculatePlus(Distance target) {
        this.distance += target.distance();
    }
}
