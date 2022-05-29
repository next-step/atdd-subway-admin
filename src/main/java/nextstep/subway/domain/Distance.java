package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    public static final int ZERO_NUM = 0;
    @Column(nullable = false)
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    public static Distance valueOf(int distance) {
        return new Distance(distance);
    }

    private void validateDistance(int distance) {
        if (isZeroOrLess(distance)) {
            throw new IllegalArgumentException("구간 길이는 0 이하가 될 수 없습니다.");
        }
    }

    private boolean isZeroOrLess(int distance) {
        return distance <= ZERO_NUM;
    }

    public int distance() {
        return distance;
    }
}
