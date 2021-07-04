package nextstep.subway.section.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance implements Serializable {

    private static final long serialVersionUID = -8710225874702467187L;

    private static final String MESSAGE_IS_POSITIVE =
        "기존 구간 안에 새 구간을 추가하는 경우, 새 구간의 길이는 기존 구간보다 크거나 같을 수 없습니다.";

    @Column(name = "distance", nullable = false)
    private final int value;

    protected Distance() {
        value = 0;
    }

    public Distance(int value) {
        verifyValueIsPositive(value);
        this.value = value;
    }

    private void verifyValueIsPositive(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException(
                MESSAGE_IS_POSITIVE);
        }
    }

    public Distance minus(int value) {
        return new Distance(this.value - value);
    }

    public Distance plus(int value) {
        return new Distance(this.value + value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distance distance = (Distance) o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
