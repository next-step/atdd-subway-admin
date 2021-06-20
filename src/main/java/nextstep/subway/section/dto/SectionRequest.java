package nextstep.subway.section.dto;

public class SectionRequest {
    private int distance;
    private Long upStationId;
    private Long downStationId;

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
