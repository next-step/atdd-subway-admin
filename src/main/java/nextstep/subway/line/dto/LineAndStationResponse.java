package nextstep.subway.line.dto;

import java.time.LocalDateTime;
import java.util.List;

import nextstep.subway.line.domain.Line;

public class LineAndStationResponse {
    private Long id;
    private String name;
    private String color;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<String> stations;

    public LineAndStationResponse(Long id, String name, String color, LocalDateTime createdDate,
        LocalDateTime modifiedDate, List<String> stationNames) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.stations = stationNames;
    }

    public static LineAndStationResponse of(Line line) {
        return new LineAndStationResponse(line.getId(), line.getName(), line.getColor(), line.getCreatedDate(),
            line.getModifiedDate(), line.getStationNames());
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

    public List<String> getStations() {
        return stations;
    }
}
