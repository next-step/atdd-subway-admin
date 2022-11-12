package nextstep.subway.dto;

public class SectionRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    public int getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }
}
