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
        validateValue(value);
        this.value = value;
    }

    private void validateValue(Integer value) {
        if (value < MIN_VALUE) {
            throw new IllegalArgumentException(String.format("최소 %d 이상의 값이여야합니다.", MIN_VALUE));
        }
    }

    public Integer getValue() {
        return value;
    }

    public Distance minus(Integer value) {
        return new Distance(this.value - value);
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
