package nextstep.subway.line.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LineRequest {

    private String name;
    private String color;
    private long upStationId;
    private long downStationId;
    private int distance;

    public Line toLine() {
        return new Line(name, color);
    }

    public Line toLineWithStations(Station upStation, Station downStation) {
        return new Line(name, color, upStation, downStation, distance);
    }
}
