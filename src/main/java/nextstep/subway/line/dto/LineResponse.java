package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stationResponses = new ArrayList<>();
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineResponse(
            Long id, String name, String color, List<StationResponse> stationResponses,
            LocalDateTime createdDate, LocalDateTime modifiedDate
    ) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stationResponses = stationResponses;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    // TODO: 향후 사라질 of
    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), null,
                line.getCreatedDate(), line.getModifiedDate());
    }

    public static LineResponse of(Line line, List<Station> stations) {
        List<StationResponse> stationsResponses = stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationsResponses,
                line.getCreatedDate(), line.getModifiedDate());
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

    public List<StationResponse> getStationResponses() {
        return stationResponses;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
