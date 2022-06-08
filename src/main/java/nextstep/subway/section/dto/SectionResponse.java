package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;

public class SectionResponse {
    private Long id;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionResponse() {
    }

    private SectionResponse(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getUpStation().getId(),section.getDownStation().getId(), section.getDistance());
    }

    public Long getId() {
        return id;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }
}
