package nextstep.subway.line.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseDTO;
import nextstep.subway.line.domain.Line;

@Builder
@Getter @NoArgsConstructor
public class LineRequest extends BaseDTO<Line> {
    private String name;
    private String color;

    private LineRequest(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    @Override
    public Line toEntity() {
        return new Line(name, color);
    }
}
