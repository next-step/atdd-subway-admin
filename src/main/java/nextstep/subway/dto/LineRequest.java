package nextstep.subway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;

import java.util.Arrays;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LineRequest {
    private String name;
    private String color;
    private int distance;
    @Builder.Default
    private int upLastStationId = 0;
    @Builder.Default
    private int downLastStationId = 0;

    public Line toLine(Station upStation, Station downStation) {
        return new Line(name, color, upStation, downStation, distance);
    }
}
