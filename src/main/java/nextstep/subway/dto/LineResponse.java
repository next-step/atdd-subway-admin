package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.*;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponseForLine> stations;

    public static LineResponse of(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                Arrays.asList(
                        new StationResponseForLine(line.getUpStation().getId(), line.getUpStation().getName()),
                        new StationResponseForLine(line.getDownStation().getId(), line.getDownStation().getName())
                ));
    }

    public LineResponse(Long id, String name, String color, List<StationResponseForLine> stations) {
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

    public List<StationResponseForLine> getStations() {
        return Collections.unmodifiableList(stations);
    }
}
