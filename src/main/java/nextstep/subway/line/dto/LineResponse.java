package nextstep.subway.line.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.subway.line.domain.Line;
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

    protected LineResponse() {
    }

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

    public static LineResponse from(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getStationResponses()
                , line.getCreatedDate(), line.getModifiedDate());
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
