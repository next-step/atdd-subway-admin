package nextstep.subway.section.dto;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;

    public SectionRequest(Long upStationId, Long downStationId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }
}
