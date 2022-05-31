package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static java.util.stream.Collectors.*;
import nextstep.subway.domain.Line;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public static LineResponse of(Line line) {
        List<StationResponse> stations = line.getStations().stream().map(StationResponse::of).collect(toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
    }

    public LineResponse() {
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

    public List<StationResponse> getStations() {
        return Collections.unmodifiableList(stations);
    }
}
