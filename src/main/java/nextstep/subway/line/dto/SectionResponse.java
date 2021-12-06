package nextstep.subway.line.dto;

import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

public class SectionResponse {
    private Long id;
    private List<StationResponse> stations;

    public SectionResponse() {
    }

    public SectionResponse(Long id, List<StationResponse> stations) {
        this.id = id;
        this.stations = stations;
    }

    public static SectionResponse of(Line line, List<Station> stations) {
        List<StationResponse> stationResponses = stations.stream()
            .map(StationResponse::of)
            .collect(Collectors.toList());
        return new SectionResponse(line.getId(), stationResponses);
    }

    public Long getId() {
        return id;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
