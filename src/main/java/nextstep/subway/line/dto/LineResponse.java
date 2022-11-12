package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<LineStationResponse> stations;

    private LineResponse(Long id, String name, String color, List<LineStationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse from(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getStationsInOrder()
                        .stream()
                        .map(station -> LineStationResponse.of(station.getId(), station.getName()))
                        .collect(Collectors.toList())
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

    public List<LineStationResponse> getStations() {
        return stations;
    }
}
