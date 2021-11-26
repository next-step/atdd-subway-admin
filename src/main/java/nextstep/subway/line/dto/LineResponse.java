package nextstep.subway.line.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    @JsonProperty("stations")
    private List<StationResponse> stationResponses;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineResponse(final Long id, final String name, final String color, final List<StationResponse> stationResponses
            , final LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stationResponses = stationResponses;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static List<LineResponse> from(final List<Line> line) {
        return line.stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public static LineResponse from(final Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), null, line.getCreatedDate(), line.getModifiedDate());
    }

    public static LineResponse of(final Line line, final List<Station> station) {
        List<StationResponse> stationResponses = station.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses, line.getCreatedDate(), line.getModifiedDate());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<StationResponse> getStationResponses() {
        return stationResponses;
    }
}
