package nextstep.subway.section.dto;

import nextstep.subway.line.dto.LineRequest;

public class SectionRequest {
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    private SectionRequest(){}

    public SectionRequest(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionRequest of(Long lineId, LineRequest lineRequest) {
        return new SectionRequest(lineId, lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
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

    public Integer getDistance() {
        return distance;
    }
}
