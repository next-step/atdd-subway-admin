package nextstep.subway.station.domain;

import java.util.List;
import java.util.NoSuchElementException;

public class Stations {

    List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = stations;
    }

    public static Stations create(List<Station> stations) {
        return new Stations(stations);
    }

    public Station getStationById(Long stationId) {
        return stations.stream()
                .filter(station -> station.getId() == stationId)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }
}
