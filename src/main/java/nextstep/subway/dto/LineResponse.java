package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.Arrays;
import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Line saved) {
        this.id = saved.getId();
        this.name = saved.getName();
        this.color = saved.getColor();
        this.stations = Arrays.asList(
                new StationResponse(saved.getUpStation()),
                new StationResponse(saved.getDownStation())
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

    public List<StationResponse> getStations() {
        return stations;
    }
}
