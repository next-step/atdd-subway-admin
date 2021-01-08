package nextstep.subway.line.dto;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private long distance;

    public SectionRequest(Long upStationId, Long downStationId, long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionRequest() {
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public long getDistance() {
        return distance;
    }
}
