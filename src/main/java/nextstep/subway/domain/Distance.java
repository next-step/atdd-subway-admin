package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final long MIN_DISTANCE = 1;
    @Column
    private long distance;

    protected Distance() {
    }

    Distance(long distance) {
        invalidInputCheck(distance);
        this.distance = distance;
    }

    public long getDistance() {
        return distance;
    }

    public boolean isValidDistance(long newDistance) {
        return this.distance > newDistance;
    }

    private void invalidInputCheck(long distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException("구간은 최소 " + MIN_DISTANCE + " 이상의 값이어야 합니다.");
        }
    }
}
