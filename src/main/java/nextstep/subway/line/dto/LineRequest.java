package nextstep.subway.line.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;

@Getter @NoArgsConstructor
public class LineRequest {
    private String name;
    private String color;

    public LineRequest(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public Line toLine() {
        return new Line(name, color);
    }
}
