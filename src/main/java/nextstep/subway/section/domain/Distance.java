package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    @Column(name = "distance", nullable = false)
    private int distance;

    protected Distance() {}

    private Distance(int distance) {
        this.distance = distance;
    }

    public static Distance from(int distance) {
        return new Distance(distance);
    }

    public boolean isGreaterThanOrEqualTo(Distance distance) {
        return this.distance >= distance.getDistance();
    }

    public int getDistance() {
        return distance;
    }
}
