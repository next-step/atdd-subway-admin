package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

public class LineRequest {
    private String name;

    public LineRequest() {
    }

    private LineRequest(String name) {
        this.name = name;
    }

    public static LineRequest of(String name) {
        return new LineRequest(name);
    }

    public String getName() {
        return name;
    }

    public Line toLine() {
        return new Line(name);
    }
}
