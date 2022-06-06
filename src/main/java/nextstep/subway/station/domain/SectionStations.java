package nextstep.subway.station.domain;

import java.util.List;
import java.util.Objects;

public class SectionStations {
    public static final String NON_EXIST_UP_DOWN_STATION_MSG = "해당 ID의 Station이 존재하지 않습니다.";

    private Station upStation;
    private Station downStation;

    public void findUpAndDownStations(List<Station> stations, Long upStationId, Long downStationId) {
        stations.stream()
                .forEach(station -> {
                    if (upStationId.equals(station.getId())) {
                        upStation = station;
                    }
                    if (downStationId.equals(station.getId())) {
                        downStation = station;
                    }
                });
        validLineStations();
    }

    private void validLineStations() {
        if (Objects.isNull(upStation) || Objects.isNull(downStation)) {
            throw new IllegalArgumentException(NON_EXIST_UP_DOWN_STATION_MSG);
        }
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }
}
