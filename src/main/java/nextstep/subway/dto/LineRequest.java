package nextstep.subway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LineRequest {
    private String name;
    private String color;
    private int distance;
    private int upLastStationId;
    private int downLastStationId;

    public Line toLine(Station upStation, Station downStation) {
        return new Line().builder()
                .name(name)
                .color(color)
                .distance(distance)
                .upLastStation(upStation)
                .downLastStation(downStation)
                .build();
    }
}
