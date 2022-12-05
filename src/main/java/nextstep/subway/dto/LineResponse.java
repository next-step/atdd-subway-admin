package nextstep.subway.dto;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    private int distance;

    public LineResponse(Long id, String name, String color, List<StationResponse> stations, int distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.distance = distance;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
            line.getStations().stream().map(StationResponse::of).collect(Collectors.toList()), line.totalDistance());
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

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
