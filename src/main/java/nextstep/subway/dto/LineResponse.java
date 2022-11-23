package nextstep.subway.dto;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.station.Station;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public static LineResponse of(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName().value(),
                line.getColor().value(),
                Arrays.asList(line.getUpStation(), line.getDownStation())
        );
    }

    public LineResponse(Long id, String name, String color, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
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
