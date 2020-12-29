package nextstep.subway.line.dto;

import lombok.Getter;
import nextstep.subway.line.domain.Line;

@Getter
public class LineRequest {
    private String name;
    private String color;
    private long upStationId;
    private long downStationId;
    private long distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line toLine() {
        return new Line(name, color);
    }
}
