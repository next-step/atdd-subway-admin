package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponseForLine> stations;

    private LineResponse(Long id, String name, String color, List<StationResponseForLine> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {

        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getLineStations().stream()
                        .map(it -> StationResponseForLine.of(it.getStation()))
                        .collect(Collectors.toList())
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

    public List<StationResponseForLine> getStations() {
        return Collections.unmodifiableList(stations);
    }
}
