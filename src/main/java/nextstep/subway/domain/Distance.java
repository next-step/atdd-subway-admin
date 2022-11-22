package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final int MIN_VALUE = 1;

    @Column(nullable = false)
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validate(distance);
        this.distance = distance;
    }

    private void validate(int distance) {
        if (distance < MIN_VALUE) {
            throw new IllegalArgumentException("구간거리는 " + MIN_VALUE + "보다 크거나 같아야합니다.");
        }
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
