package nextstep.subway.line.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.dto.StationResponse;

public class LineInfoResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineInfoResponse() {
    }
    
    public LineInfoResponse(Long id, String name, String color, List<StationResponse> stations, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineInfoResponse of(Line line) {
        List<StationResponse> stations = line.getStations()
                                                .stream()
                                                .map(StationResponse::of)
                                                .collect(Collectors.toList());

        return LineInfoResponse.of(line, stations);
    }

    public static LineInfoResponse of(Line line, List<StationResponse> stations) {
        return new LineInfoResponse(line.getId(), line.getName(), line.getColor(), stations, line.getCreatedDate(), line.getModifiedDate());
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

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof LineInfoResponse)) {
            return false;
        }
        LineInfoResponse lineInfoResponse = (LineInfoResponse) o;
        return Objects.equals(name, lineInfoResponse.name) && Objects.equals(color, lineInfoResponse.color) && Objects.equals(stations, lineInfoResponse.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, stations, createdDate, modifiedDate);
    }

}
