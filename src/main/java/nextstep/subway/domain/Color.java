package nextstep.subway.domain;

import javax.persistence.Embeddable;
import java.util.regex.Pattern;

@Embeddable
public class Color {

    private static final String COLOR_PATTERN = "[bg]+[-]+[\\w]+[-]+[\\d]+";

    private String color;

    protected Color() {
    }

    public Color(String color) throws IllegalArgumentException {
        validBgColor(color);
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    private void validBgColor(String color) {
        if (!Pattern.matches(COLOR_PATTERN, color)) {
            throw new IllegalArgumentException("색 형식이 올바르지 않습니다. 요청값:" + color);
        }
    }
}
