package nextstep.subway.dto;

import nextstep.subway.domain.Section;

public class SectionResponse {

    private Long id;

    private Long upStationId;

    private Long downStationId;

    private long distance;

    public SectionResponse(Long id, Long upStationId, Long downStationId, long distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(), section.getUpStation().getId()
                , section.getDownStation().getId(), section.getDistance());
    }
}
