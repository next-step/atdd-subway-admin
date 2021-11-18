package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final String DISTANCE_IS_LESS_THAN_MIN_DISTANCE_ERROR_MESSAGE = "거리의 길이는 1보다 길어야합니다. distance=%s";
    private static final int MIN_DISTANCE = 1;

    @Column(name = "distance", nullable = false)
    private int distance;

    protected Distance() {}

    private Distance(int distance) {
        this.distance = distance;
    }

    public static Distance from(int distance) {
        validateDistance(distance);
        return new Distance(distance);
    }

    public static Distance merge(Distance upDistance, Distance downDistance) {
        return Distance.from(upDistance.distance + downDistance.distance);
    }

    public boolean isGreaterThanOrEqualTo(Distance distance) {
        return this.distance >= distance.getDistance();
    }

    public int getDistance() {
        return distance;
    }

    private static void validateDistance(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException(String.format(DISTANCE_IS_LESS_THAN_MIN_DISTANCE_ERROR_MESSAGE, distance));
        }
    }
}
