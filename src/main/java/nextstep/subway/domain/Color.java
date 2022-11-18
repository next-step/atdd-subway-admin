package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Color {
    @Column(unique = true, nullable = false)
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
