package nextstep.subway.line.domain;

import javax.persistence.Embeddable;

import static nextstep.subway.line.application.exception.InvalidDistanceException.error;

@Embeddable
public class Distance {

    public static final int MIN_DISTANCE = 0;
    public static final String SHORT_MIN_DISTANCE = "지하철 구간 사이의 거리는 " + MIN_DISTANCE + "보다 커야 합니다.";
    public static final String LONG_DISTANCE_BETWEEN_SECTION = "구간 거리가 기존 역 사이의 거리보다 작야아 합니다.";

    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if (distance <= MIN_DISTANCE) {
            throw error(SHORT_MIN_DISTANCE);
        }
    }

    public boolean divisible(Section section) {
        if (distance <= section.getDistance()) {
            throw error(LONG_DISTANCE_BETWEEN_SECTION);
        }
        return true;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public void minus(int distance) {
        this.distance -= distance;
    }
}
