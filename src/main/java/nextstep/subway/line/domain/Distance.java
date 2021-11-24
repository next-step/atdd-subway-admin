package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column(name = "distance")
    private Integer value;

    protected Distance() {
    }

    private Distance(Integer value) {
        validate(value);

        this.value = value;
    }

    public static Distance valueOf(Integer value) {
        return new Distance(value);
    }

    public void validate(Integer value) {
        checkNegativeValue(value);
        checkZeroValue(value);
    }

    private void checkZeroValue(Integer value) {
        if (value == 0) {
            throw new IllegalArgumentException("0값이 입력되었습니다.");
        }
    }

    private void checkNegativeValue(Integer value) {
        if (value < 0) {
            throw new IllegalArgumentException("음수값이 입력되었습니다.");
        }
    }

    public void minus(Distance distance) {
        if (this.value < distance.value) {
            throw new IllegalArgumentException("기존구간 길이보다 긴 구간의 길이를 뺄 수 없습니다.");
        }

        this.value -= distance.value;
    }

    public void plus(Distance distance) {
        this.value += distance.value;
    }

    public Integer value() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Distance)) {
            return false;
        }
        Distance distance = (Distance) o;
        return Objects.equals(value, distance.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
