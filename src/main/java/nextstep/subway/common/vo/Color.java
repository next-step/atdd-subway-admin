package nextstep.subway.common.vo;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
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
}
