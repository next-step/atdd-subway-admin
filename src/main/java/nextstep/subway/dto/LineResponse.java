package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.List;
import java.util.Map;

public class LineResponse {

    private final Long id;

    private final String name;

    private final String color;

    private final List<Map<String, Object>> stations;

    public LineResponse(Long id, String name, String color, List<Map<String, Object>> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        List<Map<String, Object>> stations = line.getStations();
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

    public List<Map<String, Object>> getStations() {
        return stations;
    }
}
