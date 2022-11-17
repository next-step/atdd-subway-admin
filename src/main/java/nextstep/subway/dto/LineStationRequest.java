package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.Station;

public class LineStationRequest {
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

    public LineStation toLineStation(Line line, Station upStation, Station downStation, int distance){
        return new LineStation(line, upStation, downStation, distance);
    }
}
