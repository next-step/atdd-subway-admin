package nextstep.subway.dto;

import nextstep.subway.domain.*;

public class LineCreateRequest {

    private final String name;
    private final String color;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public LineCreateRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public int getDistance() {
        return distance;
    }

    public Line toLine(Station upStation, Station downStation) {
        return new Line(new Name(name), new Color(color), upStation, downStation, new Distance(distance));
    }
}
