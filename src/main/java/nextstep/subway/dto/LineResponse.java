package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {

    private Long id;

    private String name;

    private String color;

    private List<StationResponse> stations = new ArrayList<>();

    public LineResponse() {
    }

    public static LineResponse of(Line line) {
        List<StationResponse> stations = new ArrayList<>();
        stations.add(StationResponse.of(line.getUpStation()));
        stations.addAll(line.getSections().stream()
                .map(section -> StationResponse.of(section.getDownStation()))
                .collect(Collectors.toList()));
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
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
        return stations;
    }
}
