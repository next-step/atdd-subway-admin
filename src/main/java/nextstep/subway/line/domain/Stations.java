package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.util.ArrayList;
import java.util.List;

public class Stations {
    private List<Station> values;

    public Stations() {
    }

    private Stations(List<Station> stations) {
        this.values = stations;
    }

    public static Stations of(List<Station> stations) {
        return new Stations(stations);
    }

    public List<Station> getStations() {
        return values;
    }

    public List<StationResponse> toStationResponses() {
        List<StationResponse> stationResponses = new ArrayList<>();

        for (Station station : values) {
            stationResponses.add(StationResponse.of(station));
        }

        return stationResponses;
    }
}
