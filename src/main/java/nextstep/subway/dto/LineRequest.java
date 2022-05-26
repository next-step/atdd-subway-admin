package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineRequest {
    private String name;

    public LineRequest() {

    }

    public LineRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Line toLine() {
        return new Line(name);
    }
}
