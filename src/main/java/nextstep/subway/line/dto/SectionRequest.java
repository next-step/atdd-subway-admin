package nextstep.subway.line.dto;

import nextstep.subway.common.exception.BadRequestException;

public class SectionRequest {
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    private SectionRequest(){}

    private SectionRequest(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionRequest of(Long lineId, LineRequest lineRequest) {
        if (lineRequest.getUpStationId().equals(lineRequest.getDownStationId())) {
            throw new BadRequestException("UpStation and DownStation should not be same");
        }
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
