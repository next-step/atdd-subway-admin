package nextstep.subway.line.dto;

import nextstep.subway.station.dto.StationResponse;

import java.util.List;

public class SectionResponse {
    private Long id;
    private List<StationResponse> stations;

    private SectionResponse(Long id, List<StationResponse> stations) {
        this.id = id;
        this.stations = stations;
    }

    public Long getId() {
        return id;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
