package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {

    private static final int MIN_VALUE = 1;

    @Column
    private Integer value;

    protected Distance() {
    }

    public Distance(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void minus(Distance distance) {
        int result = this.value - distance.getValue();
        if (result < MIN_VALUE) {
            throw new IllegalArgumentException("두 노선간의 거리의 차가 음수가 될 수 없습니다.");
        }

        this.value = result;
    }

    public void plus(Distance distance) {
        this.value += distance.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance = (Distance) o;
        return value.equals(distance.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
