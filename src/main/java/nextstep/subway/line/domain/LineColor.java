package nextstep.subway.line.domain;

import nextstep.subway.common.exception.InvalidEntityRequiredException;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class LineColor {
    private String color;

    protected LineColor() {
    }

    public LineColor(String color) {
        validateEmptyColor(color);
        this.color = color;
    }

    private void validateEmptyColor(String color) {
        if (color.isEmpty()) {
            throw new InvalidEntityRequiredException(color);
        }
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineColor lineColor = (LineColor) o;
        return Objects.equals(color, lineColor.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}
