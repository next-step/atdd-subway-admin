package nextstep.subway.line.domain;

import nextstep.subway.consts.ErrorMessage;

import javax.persistence.Column;

public class LineColor {
    @Column
    String color;

    protected LineColor() {
    }

    private LineColor(String color) {
        validateLineColor(color);
        this.color = color;
    }

    private void validateLineColor(String color) {
        if (color == null || color.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.ERROR_LINECOLOR_EMPTY);
        }
    }

    public static LineColor from(String color) {
        return new LineColor(color);
    }

    public String getColor() {
        return color;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof LineColor)) {
            return false;
        }
        return ((LineColor)obj).getColor() == color;
    }
}
