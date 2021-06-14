package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Color {

    @Column
    private String color;

    protected Color() {
    }

    public Color(String color) {
        if (color == null || color.isEmpty()) {
            throw new IllegalArgumentException();
        }

        this.color = color;
    }

    @Override
    public String toString() {
        return color;
    }

}
