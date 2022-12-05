package nextstep.subway.domain.station;

import nextstep.subway.constants.ErrorMessage;
import nextstep.subway.domain.line.LineStation;

public class StationStatus {

    private Station station;
    private LineStation lineStation;
    private StationPosition stationPosition;

    public StationStatus(Station station) {
        this.station = station;
    }

    public StationStatus(Station station, LineStation lineStation, StationPosition stationPosition) {
        this.station = station;
        this.lineStation = lineStation;
        this.stationPosition = stationPosition;
    }

    public boolean matchStationPosition(StationPosition stationPosition) {
        return stationPosition.equals(this.stationPosition);
    }

    public void distanceValidation(int distance) {
        if (lineStation.distanceCompare(distance) <= 0) {
            throw new IllegalArgumentException(ErrorMessage.SECTION_DISTANCE_NOT_VALID);
        }
    }
}
