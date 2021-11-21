package nextstep.subway.line.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.dto.StationResponse;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(final Long id, final String name, final String color, final LocalDateTime createdDate,
        final LocalDateTime modifiedDate, final List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.stations = stations;
    }

    public static List<LineResponse> of(final List<Line> lines) {
        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public static LineResponse of(final Line line) {
        return new LineResponse(
            line.getId(), line.getName(), line.getColor(), line.getCreatedDate(), line.getModifiedDate(),
            StationResponse.of(line.computeSortedStations())
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final LineResponse that = (LineResponse)obj;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name)
            && Objects.equals(color, that.color) && Objects.equals(createdDate, that.createdDate)
            && Objects.equals(modifiedDate, that.modifiedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, createdDate, modifiedDate);
    }
}
