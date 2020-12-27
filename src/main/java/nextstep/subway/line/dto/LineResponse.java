package nextstep.subway.line.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;

import java.time.LocalDateTime;
import nextstep.subway.station.dto.StationResponse;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getCreatedDate(), line.getModifiedDate());
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
