package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private Set<StationResponse> stations = new LinkedHashSet<>();

    private LineResponse() {}

    public LineResponse(Long id, String name, String color, Set<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        Set<Station> stations = line.getSections().sortStations();
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                stations
                        .stream()
                        .map(StationResponse::of)
                        .collect(Collectors.toSet()));
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

    public Set<StationResponse> getStations() {
        return stations;
    }

}
