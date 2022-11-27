package nextstep.subway.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionRequest {
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.distance = distance;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
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