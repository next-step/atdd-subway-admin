package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest() {
    }

    private LineRequest(String name, String color, Long upStationId, Long downStationId) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public static LineRequest toCreateRequestParameter(String name, String color, Long upStationId, Long downStationId) {
        return new LineRequest(name, color, upStationId, downStationId);
    }

    public static LineRequest toUpdateRequestParameter(String name, String color) {
        return new LineRequest(name, color, null, null);
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
