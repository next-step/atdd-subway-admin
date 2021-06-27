package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;

public class SectionResponse {

    private Long id;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionResponse() {
    }

    public SectionResponse(Long id, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(
            section.getId(),
            section.getUpStationId(),
            section.getDownStationId(),
            section.getDistance()
        );
    }

}
