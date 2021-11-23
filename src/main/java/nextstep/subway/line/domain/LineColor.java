package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.springframework.util.StringUtils;

@Embeddable
public class LineColor {

    @Column(name = "color")
    private String value;

    protected LineColor() {
    }

    private LineColor(String value) {
        validate(value);
        this.value = value;
    }

    public static LineColor from(String value) {
        return new LineColor(value);
    }

    private void validate(String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("라인 색상은 빈 값일 수 없습니다.");
        }
    }

    public String getValue() {
        return value;
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
        return Objects.equals(value, lineColor.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
