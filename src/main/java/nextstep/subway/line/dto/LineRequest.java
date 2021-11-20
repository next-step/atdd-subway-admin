package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

public class LineRequest {

    private String name;
    private String color;

    public LineRequest(String name, String color) {
        validateLineRequest(name, color);
        this.name = name;
        this.color = color;
    }

    private void validateLineRequest(String name, String color) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name값은 null 또는 빈값을 넣으면 안됩니다.");
        }
        if (color == null || color.isEmpty()) {
            throw new IllegalArgumentException("color값은 null 또는 빈값을 넣으면 안됩니다.");
        }
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Line toLine() {
        return new Line(name, color);
    }
}
