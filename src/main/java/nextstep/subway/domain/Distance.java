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

    public int getDistance() {
        return distance;
    }

    public Distance substract(Distance distance) {
        return new Distance(this.distance - distance.distance);
    }

    public boolean isSameOrLonger(Distance distance) {
        return substract(distance).distance <= 0;
    }

    public void add(Distance distance) {
        this.distance += distance.distance;
    }
}
