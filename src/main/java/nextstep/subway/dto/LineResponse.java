package nextstep.subway.dto;

import java.util.List;
import nextstep.subway.domain.Line;

public class LineResponse {
    private Long id;

    private String name;

    private String color;

    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return this.color;
    }

    public List<StationResponse> getStations() {
        return this.stations;
    }

    public void setStations(List<StationResponse> stations) {
        this.stations = stations;
    }
}
