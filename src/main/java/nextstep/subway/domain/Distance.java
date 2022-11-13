package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column(name = "distance")
    private int value;

    public Distance(final int value) {
        this.value = value;
    }

    protected Distance() {

    }

    public void change(final int distance) {
        this.value = distance;
    }

    public void equalToOrLessThan(final int distance) {
        if (this.value <= distance) {
            throw new IllegalArgumentException("상행역 또는 하행역이 동일한 경우, 기존 구간의 거리보다 짧아야 합니다.");
        }
    }

    public int getValue() {
        return value;
    }
}
