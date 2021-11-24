package nextstep.subway.station.dto;

import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Stations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StationResponses {
    private final List<StationResponse> stationResponses;

    private StationResponses(List<StationResponse> stationResponses) {
        this.stationResponses = stationResponses;
    }

    public static StationResponses ofByLineSections(Sections sections) {
        return of(sections.getStations());
    }

    private static StationResponses of(Stations stations) {
        return new StationResponses(stations.getStations()
                .stream()
                .map(StationResponse::of)
                .collect(Collectors.toList()));
    }

    public List<StationResponse> getStationResponses() {
        return new ArrayList<>(stationResponses);
    }
}
