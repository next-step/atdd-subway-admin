package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.List;

public class LineResponse {
    private final Long id;
    private final String name;
    private final List<StationResponse> stations;

    public static LineResponse of(Line line, List<StationResponse> stations) {
        return new LineResponse(line.getId(), line.getName(), stations);
    }

    public LineResponse(Long id, String name, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.stations = stations;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
