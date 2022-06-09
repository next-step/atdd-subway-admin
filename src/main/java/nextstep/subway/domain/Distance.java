package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column(nullable = false)
    private Long value;

    protected Distance() {}

    public Distance(Long value) {
        checkValidateDistance(value);

        this.value = value;
    }

    public void subtract(Distance distance) {
        this.value -= distance.value;
    }

    public void add(Distance distance) {
        this.value += distance.value;
    }

    public boolean isLessThenOrSame(Distance distance) {
        return this.value <= distance.value;
    }

    private void checkValidateDistance(Long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("거리는 0 보다 커야 합니다.");
        }
    }

    public Long getValue() {
        return value;
    }
}
