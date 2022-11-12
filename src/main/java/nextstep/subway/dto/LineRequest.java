package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineRequest {

    private String name;

    private String color;

    private int distance;

    private Long upStationId;

    private Long downStationId;

    public LineRequest() {
    }

    public LineRequest(String name, String color, int distance, Long upStationId, Long downStationId) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Line toLine(Station upStation, Station downStation) {
        return new Line(this.name, this.color, this.distance, upStation, downStation);
    }

    public LineRequest modify(String name, String color) {
        this.name = name;
        this.color = color;
        return this;
    }
}
