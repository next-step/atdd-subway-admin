package nextstep.subway.line.dto;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.List;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

public class LineAndStationResponse {
    private Long id;
    private String name;
    private String color;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<StationResponse> stations;

    public LineAndStationResponse(Long id, String name, String color, LocalDateTime createdDate,
        LocalDateTime modifiedDate, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.stations = createStationResponses(stations);
    }

    private List<StationResponse> createStationResponses(List<Station> stations) {
        return stations.stream().map(StationResponse::of).collect(toList());
    }

    public static LineAndStationResponse of(Line line) {
        return new LineAndStationResponse(line.getId(), line.getName(), line.getColor(), line.getCreatedDate(),
            line.getModifiedDate(), line.getStations());
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
