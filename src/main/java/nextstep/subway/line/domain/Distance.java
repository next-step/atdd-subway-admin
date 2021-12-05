package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final String ERROR_INVALID_DISTANCE = "구간의 거리가 같거나 더 멉니다.";

    @Column(name = "distance")
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        this.distance = distance;
    }

    public Distance minus(Distance target) {
        if (isSameOrFarther(target)) {
            throw new IllegalArgumentException(ERROR_INVALID_DISTANCE);
        }
        return new Distance(this.distance - target.distance);
    }

    private boolean isSameOrFarther(Distance target) {
        return this.distance <= target.distance;
    }

    public int getDistance() {
        return distance;
    }
}
