package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column(nullable = false)
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        this.distance = distance;
    }

    public void add(int distance) {
        this.distance += distance;
    }

    public Distance subtract(Distance distance) {
        return new Distance(this.distance - distance.value());
    }

    public int value() {
        return distance;
    }
}
