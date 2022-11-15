package nextstep.subway.distance;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column
    private int value;

    protected Distance() {
    }

    public Distance(int distance) {
        validate(distance);
        this.value = distance;
    }

    private void validate(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("거리는 0보다 작을 수 없습니다.");
        }
    }

    public int getValue() {
        return value;
    }

    public Distance subtract(int distance) {
        return new Distance(this.value - distance);
    }

    public void add(Distance distance) {
        this.value += distance.value;
    }
}
