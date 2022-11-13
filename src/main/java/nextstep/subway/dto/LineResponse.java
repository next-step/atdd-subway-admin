package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.List;

import nextstep.subway.domain.Line;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations = new ArrayList<>();

    public static LineResponse of(Line line) {
        LineResponse response = new LineResponse();
        response.id = line.getId();
        response.name = line.getName();
        response.color = line.getColor();
        response.stations.add(new StationResponse(line.getUpStation().getId(), line.getUpStation().getName()));
        response.stations.add(new StationResponse(line.getDownStation().getId(), line.getDownStation().getName()));
        return response;
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
