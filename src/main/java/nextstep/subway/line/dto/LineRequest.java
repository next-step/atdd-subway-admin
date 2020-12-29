package nextstep.subway.line.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public LineRequest(String name, String color, Long upStationId, Long downStationId, Long distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toLine() {
        return new Line(name, color);
    }

    public Line toLineWithStation(Station upStation, Station downStation) {
        return new Line(getName(), getColor(), upStation, downStation, getDistance());
    }

    public boolean isContainsStation() {
        return upStationId != null && downStationId != null;
    }
}
