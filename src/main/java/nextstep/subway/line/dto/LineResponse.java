package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.time.LocalDateTime;
import java.util.List;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import static java.util.stream.Collectors.toList;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineResponse() {
    }

    private LineResponse(Long id, String name, String color, List<StationResponse> stations,
        LocalDateTime createdDate, LocalDateTime modifiedDate) {

        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(),
            line.getName().getValue(),
            line.getColor().getValue(),
            StationResponse.of(
                line.getSections().extractStationsWithOrdering()),
            line.getCreatedDate(),
            line.getModifiedDate());
    }

    public static LineResponse of(Line line, List<Station> stations) {
        return new LineResponse(line.getId(),
            line.getName().getValue(),
            line.getColor().getValue(),
            StationResponse.of(stations),
            line.getCreatedDate(),
            line.getModifiedDate());
    }

    public static List<LineResponse> of(List<Line> lines) {
        return lines.stream()
            .map(LineResponse::of)
            .collect(toList());
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
