package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<Map<String, Object>> stations;

    public static LineResponse of(Line line) {
        List<Map<String, Object>> responses = line.getStations()
                .stream()
                .map(station -> StationResponse.of(station).toLineResponseOfStations())
                .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), responses);
    }

    public LineResponse(Long id, String name, String color, List<Map<String, Object>> stations) {
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

    public List<Map<String, Object>> getStations() {
        return Collections.unmodifiableList(stations);
    }
}
