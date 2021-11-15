package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.springframework.util.StringUtils;

@Embeddable
public class LineColor {
    private static final String EMPTY_LINE_COLOR_ERROR_MESSAGE = "노선색깔이 비어있습니다. color=%s";

    @Column(name = "color", nullable = false)
    private String color;

    protected LineColor() {}

    private LineColor(String color) {
        this.color = color;
    }

    public static LineColor from(String color) {
        validateLineColor(color);
        return new LineColor(color);
    }

    public String getValue() {
        return color;
    }

    private static void validateLineColor(String color) {
        if (!StringUtils.hasLength(color)) {
            throw new IllegalArgumentException(String.format(EMPTY_LINE_COLOR_ERROR_MESSAGE, color));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LineColor lineColor = (LineColor)o;
        return Objects.equals(color, lineColor.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}
