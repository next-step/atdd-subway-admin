package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.ArrayList;
import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations = new ArrayList<>();

    private LineResponse() {
    }

    private LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public LineResponse of(Line line) {
        stations.add(StationResponse.of(line.upStation()));
        stations.add(StationResponse.of(line.downStation()));
        return new LineResponse(line.id(), line.name(), line.color(), stations);
    }
}
