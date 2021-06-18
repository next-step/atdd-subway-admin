package nextstep.subway.line.dto;

import javax.validation.constraints.NotBlank;

import nextstep.subway.line.domain.Line;

public class LineRequest extends SectionRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String color;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        super();
        this.name = name;
        this.color = color;
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        super(upStationId, downStationId, distance);
        this.name = name;
        this.color = color;
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
