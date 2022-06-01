package nextstep.subway.dto;

public class LineRequest {
    private String name;

    private Long upStationId;

    private Long downStationId;

    private String color;

    private Long distance;

    public LineRequest() {
    }

    public LineRequest(String name, Long upStationId, Long downStationId, String color, Long distance) {
        this.name = name;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.color = color;
        this.distance = distance;
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

    public String getColor() {
        return color;
    }

    public Long getDistance() {
        return distance;
    }
}
