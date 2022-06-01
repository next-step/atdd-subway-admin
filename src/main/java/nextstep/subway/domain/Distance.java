package nextstep.subway.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    public static final int CRITICAL_POINT = 0;

    private int value;

    protected Distance() {}

    public Distance(int distance) {
        this.value = distance;
        validateDistance();
    }

    public void minus(Distance distance) {
        this.value -= distance.value;
        validateDistance();
    }

    private void validateDistance() {
        if (value <= CRITICAL_POINT) {
            throw new IllegalArgumentException("구간의 길이는 0보다 작거나 같을 수 없습니다.");
        }
    }

    public int value() {
        return value;
    }
}
