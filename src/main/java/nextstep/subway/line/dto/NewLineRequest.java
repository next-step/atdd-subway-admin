package nextstep.subway.line.dto;

public class NewLineRequest {
    private String name;
    private String color;
    private long upStationId;
    private long downStationId;
    private int stationDistance;

    public NewLineRequest() {
    }

    public NewLineRequest(String name, String color, long upStationId, long downStationId, int stationDistance) {
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
