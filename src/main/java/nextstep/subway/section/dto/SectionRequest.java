package nextstep.subway.section.dto;

import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.station.domain.Station;

public class SectionRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;


    public SectionRequest() {
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

    public LineStation toLineStation(Station upStation, Station downStation) {
        return new LineStation(downStation, upStation, distance);
    }
}
