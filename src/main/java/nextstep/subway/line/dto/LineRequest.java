package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineColor;
import nextstep.subway.line.domain.LineName;
import nextstep.subway.station.domain.Station;

public class LineRequest {
    private LineName name;
    private LineColor color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = new LineName(name);
        this.color = new LineColor(color);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public LineName getLineName() {
        return name;
    }

    public String getName() {
        return name.getName();
    }

    public String getColor() {
        return color.getColor();
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

    public boolean isSameName(String lineName) {
        return getName().equals(lineName);
    }

    public Line toLine(Station upStation, Station downStation) {
        return Line.of(getName(), getColor(), upStation, downStation, distance);
    }
}
