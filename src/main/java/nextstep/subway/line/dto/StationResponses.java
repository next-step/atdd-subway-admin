package nextstep.subway.line.dto;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

public final class StationResponses {
    private final List<StationResponse> stationResponses;

    private StationResponses(List<StationResponse> stationResponses) {
        this.stationResponses = stationResponses;
    }

    public static StationResponses create(List<Station> stations) {
        return new StationResponses(
            stations.stream()
                .map(station -> StationResponse.of(station))
                .collect(Collectors.toList()));
    }

    public List<StationResponse> getStationResponses() {
        return stationResponses;
    }
}
