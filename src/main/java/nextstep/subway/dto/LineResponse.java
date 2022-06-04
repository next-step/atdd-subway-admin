package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private final Long id;

    private final String name;

    private final String color;

    private final List<LineStationResponse> lineStations;
    private final List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, List<LineStationResponse> lineStations, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.lineStations = lineStations;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        List<LineStationResponse> lineStations = line.getLineStations()
                .stream()
                .map(LineStationResponse::of)
                .collect(Collectors.toList());

        List<StationResponse> stations = new ArrayList<>();
        lineStations.forEach(lineStation -> {
            stations.addAll(lineStation.getStations());
        });

        return new LineResponse(line.getId(), line.getName(), line.getColor(), lineStations, stations.stream().distinct().collect(Collectors.toList()));
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

    public List<LineStationResponse> getLineStations() {
        return lineStations;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
