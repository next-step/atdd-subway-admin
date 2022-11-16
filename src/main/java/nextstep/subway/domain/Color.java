package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Color {

    private static final String ROUTE_COLOR_ERROR = "노선 색상을 지정해주세요.";

    @Column(unique = true)
    private String color;

    protected Color() {
    }

    private Color(String color) {
        this.color = color;
    }

    public static Color from(String color) {
        if (Objects.isNull(color) || color.trim().isEmpty()) {
            throw new IllegalArgumentException(ROUTE_COLOR_ERROR);
        }
        return new Color(color);
    }

    public String value() {
        return color;
    }
}
