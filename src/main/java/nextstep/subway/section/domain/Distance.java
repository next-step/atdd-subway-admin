package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    @Column(name = "distance")
    private int value;

    protected Distance() {

    }

    public Distance(int value) {
        validateIllegalConstructor(value);
        this.value = value;
    }

    private void validateIllegalConstructor(int value) {
        if(value <= 0) {
            throw new IllegalArgumentException("거리는 0이하가 될 수 없습니다.");
        }
    }

    public void minus(Distance distance) {
        validateEnoughDistance(distance);
        this.value -= distance.value;
    }

    private void validateEnoughDistance(Distance distance) {
        if(this.value - distance.value <= 0) {
            throw new IllegalArgumentException("거리는 0이하가 될 수 없습니다.");
        }
    }
}
