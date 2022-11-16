package nextstep.subway.dto;

public class SectionResponse {

    public Long lineId;
    private Long sectionId;

    private Long upStationId;

    private Long downStationId;

    private Long distance;

    public SectionResponse() {
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
        return distance;
    }
}
