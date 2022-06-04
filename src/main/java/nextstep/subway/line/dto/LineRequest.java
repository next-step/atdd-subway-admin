package nextstep.subway.line.dto;

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
}
