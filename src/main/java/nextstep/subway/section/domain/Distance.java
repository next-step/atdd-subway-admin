package nextstep.subway.section.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final String INVALID_DISTANCE = "잘못된 거리 입력입니다";

    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validate(distance);
        this.distance = distance;
    }

    private void validate(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException(INVALID_DISTANCE);
        }
    }

    public int getDistance() {
        return distance;
    }

    public void checkDistanceUpdate(Section section) {
        if (distance <= section.getDistance()) {
            throw new IllegalArgumentException(INVALID_DISTANCE);
        }
    }
}
