package nextstep.subway.dto;

public class SectionRequest {
    private Long distance;

    private Long upStationId;

    private Long downStationId;

    public Long getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }
}
