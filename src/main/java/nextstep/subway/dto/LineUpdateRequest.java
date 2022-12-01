package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineUpdateRequest {
    private String name;
    private String color;

    public LineUpdateRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Line toLine(long id) {
        return new Line(id, name, color);
    }
}
