package nextstep.subway.dto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        List<StationResponse> stations = mapToStationResponse(line);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
    }

    private static List<StationResponse> mapToStationResponse(Line line) {
        return line.getSections().stream()
                .flatMap(LineResponse::mapToStationResponse)
                .collect(Collectors.toList());
    }

    private static Stream<StationResponse> mapToStationResponse(Section section) {
        return section.allStations()
                .stream()
                .map(StationResponse::of);
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
