package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

public class LineRequest {

    private final String name;

    private final String color;

    private final Long upStationId;

    private final Long downStationId;

    private final int distance;

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
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

    public Line toLine() {
        return new Line(name, color);
    }
}
