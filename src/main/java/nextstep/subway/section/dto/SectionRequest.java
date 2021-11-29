package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

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
