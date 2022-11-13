package nextstep.subway.dto;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Color;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Name;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    private Long distance;

    private LineResponse(Long id, Name name, Color color, List<StationResponse> stations, Distance distance) {
        this.id = id;
        this.name = name.value();
        this.color = color.value();
        this.stations = stations;
        this.distance = distance.value();
    }

    public static LineResponse from(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
                line.findStations().stream().map(StationResponse::from).collect(Collectors.toList()), line.getDistance());
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

    public Long getDistance() {
        return distance;
    }
}
