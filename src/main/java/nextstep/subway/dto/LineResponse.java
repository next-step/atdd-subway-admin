package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;

    public static LineResponse from(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), toStationResponse(line),
                line.getCreatedDate(), line.getModifiedDate());
    }

    public LineResponse(final Long id, final String name, final String color, final List<StationResponse> stations,
                        final LocalDateTime createdDate, final LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
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

    private static List<StationResponse> toStationResponse(final Line line) {
        return line.getSections()
                .stream()
                .flatMap(section -> section.getStations().stream())
                .distinct()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }
}
