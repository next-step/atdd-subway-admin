package nextstep.subway.dto;

public class SectionRequest {
    private Long downStationId;
    private Long upStationId;
    private Long distance;

    public SectionRequest(long l, long l1, long l2) {
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDistance() {
        return distance;
    }
}
