package nextstep.subway.line.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;

@Getter
@NoArgsConstructor
public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Line toLine() {
        if (upStationId == null || downStationId == null) {
            return new Line(name, color);
        }

        Section section = new Section(upStationId, downStationId, distance);
        return new Line(name, color, new Sections(section));
    }
}
