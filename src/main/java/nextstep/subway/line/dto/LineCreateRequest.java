package nextstep.subway.line.dto;

public class LineCreateRequest {
    private String name;
    private String color;
    private long upStationId;
    private long downStationId;
    private int stationDistance;

    public LineCreateRequest() {
    }

    public LineCreateRequest(String name, String color, long upStationId, long downStationId, int stationDistance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.stationDistance = stationDistance;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getStationDistance() {
        return stationDistance;
    }
}
