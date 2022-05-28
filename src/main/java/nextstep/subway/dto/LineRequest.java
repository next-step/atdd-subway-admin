package nextstep.subway.dto;

public class LineRequest {

    private String name;
    private String color;
    private String upStationId;
    private String downStationId;
    private int distance;

    public LineRequest(String name, String color, int upStationId, int downStationId, int distance) {

    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getUpStationId() {
        return upStationId;
    }

    public String getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

}
