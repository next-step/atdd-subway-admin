package nextstep.subway.dto;


import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.Station;

public class LineStationRequest {
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    protected LineStationRequest() {
    }

    public LineStationRequest(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getLineId() {
        return lineId;
    }

    public LineStation toLineStation(Station upStation, Station downStation) {
        return new LineStation(upStation, downStation, distance);
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
