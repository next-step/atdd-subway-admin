package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;

public class SectionResponse {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionResponse() {}

    public SectionResponse(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionResponse from(Section section) {
        return new SectionResponse(
                section.getUpStation().getId(),
                section.getDownStation().getId(),
                section.getDistance().get()
        );
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
}
