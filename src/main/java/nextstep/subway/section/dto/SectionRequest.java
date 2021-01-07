package nextstep.subway.section.dto;

import nextstep.subway.common.exception.BadRequestException;
import nextstep.subway.line.dto.LineRequest;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    private SectionRequest(){}

    public SectionRequest(Long upStationId, Long downStationId, Integer distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionRequest of(LineRequest lineRequest) {
        return new SectionRequest(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    public void validate() {
        if (upStationId.equals(downStationId)) {
            throw new BadRequestException("UpStation and DownStation should not be same");
        }
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
