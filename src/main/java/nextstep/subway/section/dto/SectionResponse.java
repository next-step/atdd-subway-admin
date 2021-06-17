package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;

public class SectionResponse {
    private Long id;
    private Long upStationId;
    private Long downStationId;
    private Long lineId;
    private int distance;

    public SectionResponse() {
    }

    public SectionResponse(Long id, Long upStationId, Long downStationId, Long lineId, int distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.lineId = lineId;
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(), section.getUpStation().getId(), section.getDownStation().getId(), section.getLine().getId(), section.getDistance().getDistance());
    }

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getLineId() {
        return lineId;
    }

    public int getDistance() {
        return distance;
    }
}

