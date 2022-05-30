package nextstep.subway.dto;

import nextstep.subway.domain.Section;

public class SectionResponse {

    private Long id;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public Long getId() {
        return id;
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

    public SectionResponse(Long id, Long upStationId, Long downStationId, Long distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(), section.getUpStationId(),
            section.getDownStationId(), section.getDistanceValue());
    }
}
