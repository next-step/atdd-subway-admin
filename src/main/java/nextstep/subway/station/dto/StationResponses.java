package nextstep.subway.station.dto;

import nextstep.subway.station.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class StationResponses {

    private List<StationResponse> stationResponses;

    public StationResponses() {
    }

    public StationResponses(final List<Station> list) {
        this.stationResponses = list.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public List<StationResponse> getStationResponses() {
        return stationResponses;
    }

    public int size() {
        return stationResponses.size();
    }

}
