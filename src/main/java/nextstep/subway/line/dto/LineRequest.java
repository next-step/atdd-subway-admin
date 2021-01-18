package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.util.Objects;

public class LineRequest {
    private Long id;
    private String name;
    private String color;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Line toLine() {
        if(Objects.nonNull(id)) {
            return new Line(id, name, color);
        }
        return new Line(name, color);
    }
}
