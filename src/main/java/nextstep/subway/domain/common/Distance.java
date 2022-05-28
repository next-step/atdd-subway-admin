package nextstep.subway.domain.common;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private static final int MIN = 1;

    @Column(nullable = false)
    private int distance;

    public static Distance of(int distance) {
        if (distance < MIN) {
            throw new IllegalArgumentException("구간거리는 " + MIN + "보다 크거나 같아야합니다.");
        }

        return new Distance(distance);
    }

    protected Distance() {
    }

    private Distance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }
}
