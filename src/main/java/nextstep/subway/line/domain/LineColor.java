package nextstep.subway.line.domain;

import javax.persistence.Embeddable;

@Embeddable
public class LineColor {
    private String color;

    protected LineColor() {
    }

    public LineColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
