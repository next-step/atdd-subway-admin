package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public LineRequest() {
    }

    public static LineRequest of(String name, String color, Long upStationId, Long downStationId, Long distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }

    private LineRequest(String name, String color, Long upStationId, Long downStationId, Long distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toLine(Station upStation, Station downStation) {
        return Line.of(name, color, upStation, downStation, distance);
    }

    public Line toLine() {
        return Line.of(name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
        return distance;
    }
}
