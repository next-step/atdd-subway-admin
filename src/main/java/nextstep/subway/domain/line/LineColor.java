package nextstep.subway.domain.line;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class LineColor {

    @Column(name = "color", unique = true)
    private String value;

    protected LineColor() {
    }

    private LineColor(String color) {
        this.value = color;
    }

    public static LineColor of(String color) {
        if (Objects.isNull(color) || color.trim().isEmpty()) {
            throw new IllegalArgumentException("노선 색상을 지정해주세요.");
        }

        return new LineColor(color);
    }

    public String getValue() {
        return value;
    }
}
