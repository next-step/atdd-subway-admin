package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineRequest {
    private String name;
    private String color;
    private Long firstStationId;
    private Long lastStationId;
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(String name, String color, Long firstStationId, Long lastStationId, int distance) {
        this.name = name;
        this.color = color;
        this.firstStationId = firstStationId;
        this.lastStationId = lastStationId;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getFirstStationId() {
        return firstStationId;
    }

    public Long getLastStationId() {
        return lastStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Line getLine() {
        return new Line(this.name, this.color);
    }
}
