package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LineColor {

    @Column(name = "color")
    private String color;

    protected LineColor() {
    }

    public LineColor(String color) {
        this.color = color;
    }

    public void modify(String color) {
        if (color != null) {
            this.color = color;
        }
    }

    public String getColor() {
        return color;
    }
}
