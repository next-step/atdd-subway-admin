package nextstep.subway.domain.line;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class LineColor {

    @Column
    private final String color;

    protected LineColor() {
        this("");
    }

    public LineColor(String color) {
        this.color = color;
    }

    public String value() {
        return this.color;
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
        return color != null ? color.hashCode() : 0;
    }
}
