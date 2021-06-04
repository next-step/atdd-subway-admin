package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;

public class SectionResponse {
    private Long id;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public SectionResponse(Long id, Long upStationId, Long downStationId, Long distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public Long getDistance() {
        return distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(
                section.getId(),
                section.getUpStation().getId(),
                section.getDownStation().getId(),
                section.getDistance().toLong()
        );
    }
}
