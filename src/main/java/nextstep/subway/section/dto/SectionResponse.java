package nextstep.subway.section.dto;

import nextstep.subway.common.Distance;
import nextstep.subway.section.domain.Section;

public class SectionResponse {
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.upStation().getId(), section.downStation().getId(), section.distance());
    }

    public SectionResponse(Long upStationId, Long downStationId, Distance distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance.distance();
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
