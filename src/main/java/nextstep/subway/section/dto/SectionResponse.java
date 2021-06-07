package nextstep.subway.section.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SectionResponse {
    @JsonIgnore
    private Long sectionId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionResponse(Long sectionId, Long upStationId, Long downStationId, int distance) {
        this.sectionId = sectionId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getSectionId() {
        return sectionId;
    }
}
