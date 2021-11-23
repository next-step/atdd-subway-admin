package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.dto.StationResponses;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    private int totalDistance;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    private LineResponse() {
    }

    public LineResponse(Long id, String name, String color, StationResponses stations, int totalDistance, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations.getStationResponses();
        this.totalDistance = totalDistance;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineResponse of(final Line line) {
        StationResponses stationResponses = StationResponses.ofByLineSections(line.getSections());
        return new LineResponse(
                line.getId()
                , line.getName()
                , line.getColor()
                , stationResponses
                , line.totalDistance()
                , line.getCreatedDate()
                , line.getModifiedDate()
        );
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

    public int getTotalDistance() {
        return totalDistance;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineResponse that = (LineResponse) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
