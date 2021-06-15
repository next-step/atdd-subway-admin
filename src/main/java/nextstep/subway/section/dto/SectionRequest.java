package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;

public class SectionRequest {
    private int distance;
    private Long upStationId;
    private Long downStationId;

//    public SectionRequest(int distance, Long upStationId, Long downStationId) {
//        this.distance = distance;
//        this.upStationId = upStationId;
//        this.downStationId = downStationId;
//    }

    public int getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }
}
