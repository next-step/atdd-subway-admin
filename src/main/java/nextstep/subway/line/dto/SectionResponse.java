package nextstep.subway.line.dto;

public class SectionResponse {
    private Long addedStationId;

    private SectionResponse() {
    }

    public SectionResponse(Long addedStationId) {
        this.addedStationId = addedStationId;
    }

    public Long getAddedStationId() {
        return addedStationId;
    }
}