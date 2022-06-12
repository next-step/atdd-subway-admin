package nextstep.subway.domain.common;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private static final int MIN = 1;

    @Column(nullable = false)
    private int value;

    protected Distance() {
    }

    private Distance(int distance) {
        this.value = distance;
    }

    public static Distance of(int distance) {
        if (distance < MIN) {
            throw new IllegalArgumentException("구간거리는 " + MIN + "보다 크거나 같아야합니다.");
        }

        return new Distance(distance);
    }

    public int getValue() {
        return value;
    }

    public Distance add(int value) {
        return Distance.of(this.value + value);
    }

    public Distance substract(Distance distance) {
        return Distance.of(value - distance.getValue());
    }
}
