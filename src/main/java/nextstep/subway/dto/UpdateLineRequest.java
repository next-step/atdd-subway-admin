package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class UpdateLineRequest {
    private String name;
    private String color;

    protected UpdateLineRequest(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public static UpdateLineRequest of(final String name, final String color) {
        return new UpdateLineRequest(name, color);
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
