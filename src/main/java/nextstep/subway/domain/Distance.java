package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private static final int MIN = 1;

    private static final String ROUTE_DISTANCE_ERROR = "구간거리는 %d보다 크거나 같아야합니다.";

    @Column(nullable = false)
    private int distance;

    protected Distance() {
    }

    private Distance(int distance) {
        this.distance = distance;
    }

    public static Distance of(int distance) {
        if (distance < MIN) {
            throw new IllegalArgumentException(String.format(ROUTE_DISTANCE_ERROR, MIN));
        }
        return new Distance(distance);
    }

    public Distance add(Distance distance) {
        return Distance.of(this.distance + distance.value());
    }

    public Distance substract(Distance distance) {
        return Distance.of(this.distance - distance.value());
    }

    public int value() {
        return this.distance;
    }
}
