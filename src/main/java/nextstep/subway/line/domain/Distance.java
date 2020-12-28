package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.exception.DistanceOutOfRangeException;

import javax.persistence.Embeddable;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Distance {

    private static final long MIN_VALUE = 0;

    private int value;

    private Distance(final int value) {
        if (value < MIN_VALUE) {
            throw new DistanceOutOfRangeException(String.format("거리 값은 음수가 될 수 없습니다. (입력 값: %d)", value));
        }
        this.value = value;
    }

    public static Distance valueOf(final int value) {
        return new Distance(value);
    }

    public Distance subtract(final Distance other) {
        return new Distance(this.value - other.value);
    }
}
