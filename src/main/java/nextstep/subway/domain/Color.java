package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import nextstep.subway.constant.ErrorCode;
import nextstep.subway.utils.StringUtils;

@Embeddable
public class Color {

    @Column(nullable = false)
    private String color;

    protected Color() {
    }

    private Color(String color) {
        validateColorNullOrEmpty(color);
        this.color = color;
    }

    public static Color from(String color) {
        return new Color(color);
    }

    private void validateColorNullOrEmpty(String color) {
        if(StringUtils.isNullOrEmpty(color)) {
            throw new IllegalArgumentException(ErrorCode.노선색상은_비어있을_수_없음.getErrorMessage());
        }
    }

    public String value() {
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
        Color color1 = (Color) o;
        return Objects.equals(color, color1.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}
