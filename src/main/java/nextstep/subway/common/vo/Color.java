package nextstep.subway.common.vo;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class Color {

    public static final String NULL_AND_EMPTY_EXCEPTION_MESSAGE = "색상은 null 이거나 empty일 수 없습니다.";

    private String color;

    protected Color() {
    }

    public Color(String color) {
        validate(color);
        this.color = color;
    }

    private void validate(String color) {
        if (Objects.isNull(color) || color.isEmpty()) {
            throw new IllegalArgumentException(NULL_AND_EMPTY_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color color1 = (Color) o;
        return Objects.equals(color, color1.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }

    public String getColor() {
        return color;
    }
}
