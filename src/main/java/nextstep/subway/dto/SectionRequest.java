package nextstep.subway.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionRequest {

    private Long upStationId;

    private Long downStationId;

    private int distance;

    private SectionRequest() {

    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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
