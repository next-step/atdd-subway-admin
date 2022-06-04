package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {

    private final Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), toStationResponse(line));
    }

    private static List<StationResponse> toStationResponse(Line line) {
        return line.getStations().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

}
