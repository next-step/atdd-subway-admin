package nextstep.subway.line.domain;

import nextstep.subway.line.exception.LineException;
import nextstep.subway.line.exception.LineExceptionType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    public static final int MIN = 1;

    @Column(name = "distance")
    public final long value;

    protected Distance() {
        value = 0;
    }

    public Distance(final long value) {
        validate(value);
        this.value = value;
    }

    private void validate(final long value) {
        if (value < MIN) {
            throw new LineException(LineExceptionType.INVALID_DISTANCE);
        }
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Distance{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Distance distance = (Distance) o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
