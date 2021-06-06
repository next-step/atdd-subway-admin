package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.type.LineColor;

public class LineRequest {
    private String name;
    private LineColor color;

    public LineRequest() {
    }

    public LineRequest(String name, LineColor color) {
        this.name = name;
        this.color = color;
    }

    public Line toLine() {
        return new Line(name, color);
    }

    public String getName() {
        return name;
    }

    public LineColor getColor() {
        return color;
    }
}
