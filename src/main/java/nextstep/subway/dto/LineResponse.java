package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Stations;

import java.util.ArrayList;
import java.util.List;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations = new ArrayList<>();

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getStations());
    }

    private LineResponse() {}

    public LineResponse(Long id, String name, String color, Stations stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        stations.getList().stream().forEach(station ->
                this.stations.add(StationResponse.of(station)));
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

}
