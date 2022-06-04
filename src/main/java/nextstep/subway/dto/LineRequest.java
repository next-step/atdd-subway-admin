package nextstep.subway.dto;

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getDistance() {
        return distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }
}
