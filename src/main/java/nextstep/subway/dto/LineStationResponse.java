package nextstep.subway.dto;

import nextstep.subway.domain.LineStation;

public class LineStationResponse {

    private int distance;

    public LineStationResponse(int distance) {
        this.distance = distance;
    }

    public static LineStationResponse of(LineStation lineStation) {
        return new LineStationResponse(lineStation.getDistance());
    }

    public int getDistance() {
        return distance;
    }
}
