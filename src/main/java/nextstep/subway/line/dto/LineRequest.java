package nextstep.subway.line.dto;

import lombok.Getter;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

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

    public Line of(final Station upStation, final Station downStation) {
        return new Line(name, color, upStation, downStation, distance);
    }
}
