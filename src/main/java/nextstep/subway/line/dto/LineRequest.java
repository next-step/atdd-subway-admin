package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.util.Objects;

public class LineRequest implements BaseRequest {
    private String name;

    private String color;

    private Long upStationId;

    private Long downStationId;

    private int distance;

    private LineRequest() {
    }

    private LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static LineRequest of(String name, String color, Long upStationId, Long downStationId, int distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }

    public Line toLine() {
        return Line.of(name, color);
    }

    @Override
    public boolean hasDuplicateStations() {
        return Objects.equals(upStationId, downStationId);
    }

    @Override
    public Long getUpStationId() {
        return upStationId;
    }

    @Override
    public Long getDownStationId() {
        return downStationId;
    }

    @Override
    public int getDistance() {
        return distance;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }


}
