package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

public class LineRequest {
    private String name;
    private String color;

    public LineRequest() {
    }

    private LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static LineRequest of(String name, String color) {
        return new LineRequest(name, color);
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
