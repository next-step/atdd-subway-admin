package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Distance;

public class SectionRequest {
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionRequest() {}

    public SectionRequest(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Distance toDistance() {
        return new Distance(distance);
    }
}
