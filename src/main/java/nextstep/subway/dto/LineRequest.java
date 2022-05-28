package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineRequest {
    private String name;

    private String color;

    public String getName() {
        return name;
    }

    public String getColor() { return color; }

    public Line toLine() {
        return new Line(name, color);
    }

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
