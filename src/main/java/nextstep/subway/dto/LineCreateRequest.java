package nextstep.subway.dto;

public class LineCreateRequest {
    private String name;
    private String color;
    private long upStationId;
    private long downStationId;
    private int distance;

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

    public int getDistance() {
        return distance;
    }
}
