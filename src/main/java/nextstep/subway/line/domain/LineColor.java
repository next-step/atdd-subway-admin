package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class LineColor {
    private static final String LINE_COLOR_NOT_NULL = "지하철 노선 색은 빈값일 수 없습니다.";

    @Column(nullable = false)
    private String color;

    protected LineColor() {}

    private LineColor(String color) {
        this.color = color;
    }

    public static LineColor from(String color) {
        if (color == null || color.isEmpty()) {
            throw new IllegalArgumentException(LINE_COLOR_NOT_NULL);
        }
        return new LineColor(color);
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
