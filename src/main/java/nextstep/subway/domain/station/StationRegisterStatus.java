package nextstep.subway.domain.station;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.domain.line.Distance;

public class StationRegisterStatus {

    private List<StationStatus> stationStatusList = new ArrayList<>();

    public void add(StationStatus stationStatus) {
        if (stationStatus != null) {
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
}
