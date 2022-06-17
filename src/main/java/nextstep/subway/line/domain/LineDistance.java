package nextstep.subway.line.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LineDistance {
    @Column(name = "distance", nullable = false)
    private int value;

    protected LineDistance() {
        this(0);
    }

    public LineDistance(int value) {
        validate(value);
        this.value = value;
    }

    private void validate(int distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("거리는 음수가 될 수 없습니다.");
        }
    }

    public int value() {
        return value;
    }

    public LineDistance add(LineDistance lineDistance) {
        return new LineDistance(value + lineDistance.value);
    }

    public LineDistance minus(LineDistance lineDistance) {
        return new LineDistance(value - lineDistance.value);
    }

    public boolean isGreaterThan(LineDistance lineDistance) {
        return value > lineDistance.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineDistance distance = (LineDistance) o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
