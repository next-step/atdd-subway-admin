package nextstep.subway.line.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Color {

    private String color;

    protected Color() {
    }

    public Color(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
