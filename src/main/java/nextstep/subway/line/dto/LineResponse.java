package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import java.time.LocalDateTime;
import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<Station> stations;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, Stations stations, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        System.out.println(stations.getStations());
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations.getStations();
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getStations(), line.getCreatedDate(), line.getModifiedDate());
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

    public List<Station> getStations() {
        return stations;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
