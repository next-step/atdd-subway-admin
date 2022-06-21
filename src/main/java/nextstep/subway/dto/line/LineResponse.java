package nextstep.subway.dto.line;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.line.Line;
import nextstep.subway.dto.station.StationSimpleResponse;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationSimpleResponse> stations;

    private LineResponse() {
    }

    private LineResponse(Long id, String name, String color,
        List<StationSimpleResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        List<StationSimpleResponse> stations = line.getAllStations().stream()
            .map(StationSimpleResponse::of)
            .collect(Collectors.toList());
        return new LineResponse(
            line.getId(),
            line.getName(),
            line.getColor(),
            stations
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationSimpleResponse> getStations() {
        return stations;
    }
}
