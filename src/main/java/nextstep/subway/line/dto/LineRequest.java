package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import javax.validation.constraints.NotBlank;

public class LineRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String color;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Line toLine() {
        return new Line(name, color);
    }
}
