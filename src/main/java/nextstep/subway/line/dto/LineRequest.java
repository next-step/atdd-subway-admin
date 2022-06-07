package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class LineRequest {
    private String name;
    private Long upStationId;
    private Long downStationId;
    private int distance;
    private String color;

    public LineRequest() {
    }

    private LineRequest(String name, Long upStationId, Long downStationId) {
        this.name = name;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public static LineRequest of(String name, Long upStationId, Long downStationId) {
        return new LineRequest(name, upStationId, downStationId);
    }

    public String getName() {
        return name;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public String getColor() {
        return color;
    }

    public Line toLine(Station upStation, Station downStation) {
        return new Line(name, color, new Section(upStation, downStation, distance));
    }
}
