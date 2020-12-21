package nextstep.subway.line.dto;

public class SectionRequest {
    private final Long downStationId;
    private final Long upStationId;
    private final Long distance;

    public SectionRequest(final Long downStationId, final Long upStationId, final Long distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
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
