package nextstep.subway.section.dto;

public class SectionResponse {
    private String downStationId;
    private String upStationId;
    private int distance;

    public String getDownStationId() {
        return downStationId;
    }

    public String getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }
}
