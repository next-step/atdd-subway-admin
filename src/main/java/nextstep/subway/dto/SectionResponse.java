package nextstep.subway.dto;

import nextstep.subway.domain.Section;

public class SectionResponse {
    private Long lineId;
    private Long upStationId;
    private Long downStationId;

    public SectionResponse(Long lineId, Long upStationId, Long downStationId) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public static SectionResponse of(Section section) {
        Long lineId = section.getLineId();
        Long upStationId = section.getUpStationId();
        Long downStationId = section.getDownStationId();
        return new SectionResponse(lineId, upStationId, downStationId);
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
}
