package nextstep.subway.line.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class LineRequest {
    private static final int MINIMUM_DISTANCE = 1;

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Distance distance;

    public Line toLine(Station upStation, Station downStation) {
        Line line = new Line(name, color);
        line.addInitSection(new Section(line, upStation, downStation, this.distance));
        return line;
    }
}
