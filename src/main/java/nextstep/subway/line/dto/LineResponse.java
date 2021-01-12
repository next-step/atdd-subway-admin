package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import java.time.LocalDateTime;
import java.util.List;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<Station> stations;     // 상행 종점부터 하행 종점까지 목록
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

    public LineResponse(Long id, String name, String color, List<Station> stations, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this(id, name, color, createdDate, modifiedDate);
        this.stations = stations;
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
