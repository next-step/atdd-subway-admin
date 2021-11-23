package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

public class LineEditRequest {
    private String name;
    private String color;

    public LineEditRequest() {
    }

    public LineEditRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static LineEditRequest of(String name, String color) {
        return new LineEditRequest(name, color);
    }

    public Line toLine() {
        return Line.of(name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
