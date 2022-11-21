package nextstep.subway.dto;

import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.Station;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public LineStation toLineStation(Station upStation, Station downStation) {
        return new LineStation(upStation, downStation, distance);
    }
}
