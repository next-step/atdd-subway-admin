package nextstep.subway.line.dto;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.station.dto.StationResponse;

public final class StationResponses {
    private final List<StationResponse> stationResponses;

    private StationResponses(List<StationResponse> stationResponses) {
        this.stationResponses = stationResponses;
    }

    public static StationResponses create(List<LineStation> lineStations) {
        return new StationResponses(lineStations
            .stream()
            .map(section -> StationResponse.of(section.getStation()))
            .collect(Collectors.toList()));
    }

    public List<StationResponse> getStationResponses() {
        return stationResponses;
    }
}
