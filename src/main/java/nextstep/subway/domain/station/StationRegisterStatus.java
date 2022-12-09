package nextstep.subway.domain.station;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.domain.line.Distance;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineStation;

public class StationRegisterStatus {

    private List<StationStatus> stationStatusList = new ArrayList<>();

    public void add(StationStatus stationStatus) {
        if (stationStatus.positionIsNotNone()) {
            stationStatusList.add(stationStatus);
        }
    }

    public boolean isEmpty() {
        return stationStatusList.isEmpty();
    }

    public void validate(StationPosition stationPosition, Distance distance, Station interStation) {
        stationStatusList.stream()
                .filter(s -> s.matchStationPosition(stationPosition))
                .findFirst()
                .ifPresent(stationStatus -> {
                    stationStatus.distanceValidation(distance);
                    stationStatus.splitLineStation(stationPosition, interStation, distance);
                });
    }

    public LineStation createLineStation() {
        if (lineStationCreateValidate()) {
            Station upStation = getStationByPosition(StationPosition.UPSTATION);
            Station downStation = getStationByPosition(StationPosition.DOWNSTATION);
            Distance distance = distanceSum();
            Line line = commonLine();
            return new LineStation(downStation, upStation, distance, line);
        }
        return null;
    }

    private boolean lineStationCreateValidate() {
        return validateUpDownStationExist() && allPositionNotNone() && allLineMatch();
    }

    private boolean validateUpDownStationExist() {
        long upStationCount = getCountBy(StationPosition.UPSTATION);
        long downStationCount = getCountBy(StationPosition.DOWNSTATION);
        return upStationCount == 1 && downStationCount == 1;
    }

    private long getCountBy(StationPosition position) {
        return stationStatusList.stream()
                .filter(stationStatus -> stationStatus.matchStationPosition(position))
                .count();
    }

    private boolean allPositionNotNone() {
        return stationStatusList.stream()
                .noneMatch(stationStatus -> stationStatus.matchStationPosition(StationPosition.NONE));
    }

    private boolean allLineMatch() {
        return stationStatusList.stream()
                .map(StationStatus::getLineStation)
                .map(LineStation::getLine)
                .distinct()
                .count() == 1;
    }

    private Station getStationByPosition(StationPosition stationPosition) {
        return stationStatusList.stream()
                .filter(stationStatus -> stationStatus.matchStationPosition(stationPosition))
                .map(StationStatus::getStation)
                .findFirst()
                .orElse(null);
    }

    private Distance distanceSum() {
        return new Distance(stationStatusList.stream()
                .map(StationStatus::getLineStationDistance)
                .mapToInt(Distance::getDistance)
                .sum());
    }

    private Line commonLine() {
        return stationStatusList.stream()
                .map(StationStatus::getLineStation)
                .map(LineStation::getLine)
                .distinct().findFirst().orElseThrow(RuntimeException::new);
    }
}
