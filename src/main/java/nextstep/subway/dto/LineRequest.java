package nextstep.subway.dto;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this(name, color, null, null, null);
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, Long distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getDistance() {
        return distance;
    }

    public Line toLine(Station upStation, Station downStation) {
        Line line = new Line(this.name, this.color);
        line.addSection(new Section(line, upStation, downStation, Distance.of(this.distance)));
        return line;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + "\"" +
                ", \"color\":\"" + color + "\"" +
                ", \"upStationId\":" + upStationId +
                ", \"downStationId\":" + downStationId +
                ", \"distance\":" + distance +
                "}";
    }
}
