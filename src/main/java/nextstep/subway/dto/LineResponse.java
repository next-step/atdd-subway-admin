package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<LineInStationResponse> stations;

    public LineResponse(Long id, String name, String color, List<LineInStationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), createStations(line));
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

    public List<LineInStationResponse> getStations() {
        return stations;
    }

    private static List<LineInStationResponse> createStations(Line line) {
        return line.getStations()
                .stream().map(LineInStationResponse::from)
                .collect(toList());
    }
}
