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

    public static LineResponse of(Line saved) {
        LineResponse lineResponse = new LineResponse();
        lineResponse.id = saved.getId();
        lineResponse.name = saved.getName();
        lineResponse.color = saved.getColor();
        lineResponse.stations = Arrays.asList(
                StationResponse.of(saved.getUpStation()),
                StationResponse.of(saved.getDownStation())
        );
        return lineResponse;
    }
}
