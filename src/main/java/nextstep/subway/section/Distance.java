package nextstep.subway.section;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private Long distance;

    protected Distance() { }

    public Distance(Distance distance) {
        this.distance = distance.distance;
    }

    public Distance(Long distance) {
        this.distance = distance;
    }

    public Distance add(Distance distance) {
        this.distance += distance.distance;
        return this;
    }

    public Distance subtract(Distance distance) {
        this.distance -= distance.distance;
        return this;
    }

    public Boolean isZero() {
        return distance == 0;
    }

    public Boolean isNegative() {
        return distance < 0;
    }

    public Long get() {
        return distance;
    }

    public int compare(Distance distance) {
        return Long.compare(this.distance, distance.distance);
    }
}
