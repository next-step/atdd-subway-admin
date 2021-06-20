package nextstep.subway.station.dto;

import nextstep.subway.station.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class StationsResponse {

    private List<StationResponse> stations;

    public StationsResponse() {
    }

    public StationsResponse(List<StationResponse> stations) {
        this.stations = stations;
    }

    public static StationsResponse of(List<Station> stations) {
        List<StationResponse> stationsResponse = stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return new StationsResponse(stationsResponse);
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
