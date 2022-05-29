package nextstep.subway.section.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private static final int MINIMUM_DISTANCE = 1;
    private static final String DISTANCE_UNDER_MINIMUM_ERROR = "구간 거리는 1 보다 커야 합니다.";

    private int distance;

    protected Distance() {

    }

    private Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    public static Distance from(int distance) {
        return new Distance(distance);
    }

    void validateDistance(int distance) {
        if (distance < MINIMUM_DISTANCE) throw new IllegalArgumentException(DISTANCE_UNDER_MINIMUM_ERROR);
    }
}
