package nextstep.subway.line.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LineColor {
    private static final String NO_LINE_COLOR_ERROR = "지하철 노선 색이 필요 합니다.";

    @Column
    private String color;

    protected LineColor() {
    }

    private LineColor(String color) {
        validateLineColor(color);
        this.color = color;
    }

    private void validateLineColor(String color) {
        if (color == null || color.isEmpty()) {
            throw new IllegalArgumentException(NO_LINE_COLOR_ERROR);
        }
    }

    public static LineColor from(String color) {
        return new LineColor(color);
    }

    public String getValue() {
        return this.color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineColor lineColor = (LineColor) o;
        return Objects.equals(color, lineColor.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}
