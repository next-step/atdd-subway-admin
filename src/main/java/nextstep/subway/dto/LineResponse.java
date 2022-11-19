package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LineResponse {

    private final Long id;

    private final String name;

    private final String color;

    private final List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        List<StationResponse> stations = createStations(line);
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

    private static List<StationResponse> createStations(Line line) {
        return line.getStations()
                .stream()
                .map(LineResponse::createStationResponse)
                .collect(Collectors.toList());
    }

    private static StationResponse createStationResponse(Map<String, Object> station) {
        long id = (long) station.get("id");
        String name = (String) station.get("name");
        LocalDateTime createdDate = (LocalDateTime) station.get("createdDate");
        LocalDateTime modifiedDate = (LocalDateTime) station.get("modifiedDate");
        return new StationResponse(id, name, createdDate, modifiedDate);
    }
}
