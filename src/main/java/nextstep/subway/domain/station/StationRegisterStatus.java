package nextstep.subway.domain.station;

import java.util.ArrayList;
import java.util.List;

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

    public void validate(StationPosition stationPosition, int distance) {
        stationStatusList.stream()
                .filter(s -> s.matchStationPosition(stationPosition))
                .findFirst()
                .ifPresent(stationStatus -> stationStatus.distanceValidation(distance));
    }
}
