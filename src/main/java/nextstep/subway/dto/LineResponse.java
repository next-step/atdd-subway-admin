package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.List;

public class LineResponse {

    private Long id;

    private String name;

    private String color;

    private List<StationResponse> stations;

    public static LineResponse of (Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getStationResponses());
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
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

    public List<StationResponse> getStationResponses() {
        return stations;
    }
}
