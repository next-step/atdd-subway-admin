package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.domain.Color;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Name;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    private LineResponse(Long id, Name name, Color color, List<StationResponse> stations) {
        this.id = id;
        this.name = name.value();
        this.color = color.value();
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        List<StationResponse> stations = new ArrayList<>();
        stations.add(StationResponse.of(line.getUpStation()));
        stations.add(StationResponse.of(line.getDownStation()));
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
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
