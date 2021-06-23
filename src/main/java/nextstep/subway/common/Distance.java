package nextstep.subway.common;

import nextstep.subway.exception.SectionCreateFailException;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final String INVALIDATE_DISTANCE_MESSAGE = "구간 사이의 거리는 0보다 커야 합니다.";
    private static final String DISTANCE_TO_LONG_MESSAGE = "기존 구간보다 긴 구간은 추가할 수 없습니다.";

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
            throw new IllegalArgumentException(INVALIDATE_DISTANCE_MESSAGE);
        }
    }

    public Distance minusDistance(Distance minusDistance) {
        compareDistance(minusDistance);
        return new Distance(this.distance - minusDistance.distance);
    }

    public Distance plusDistance(Distance plusDistance) {
        return new Distance(this.distance + plusDistance.distance);
    }

    private void compareDistance(Distance compareDistance) {
        if (this.distance <= compareDistance.distance) {
            throw new SectionCreateFailException(DISTANCE_TO_LONG_MESSAGE);
        }
    }

    public int distance() {
        return distance;
    }
}
