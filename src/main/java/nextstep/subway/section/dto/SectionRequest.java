package nextstep.subway.section.dto;

public class SectionRequest {
    private String upStationId;
    private String downStationId;
    private int distance;

    public String getUpStationId() {
        return upStationId;
    }

    public void setUpStationId(String upStationId) {
        this.upStationId = upStationId;
    }

    public String getDownStationId() {
        return downStationId;
    }

    public void setDownStationId(String downStationId) {
        this.downStationId = downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
