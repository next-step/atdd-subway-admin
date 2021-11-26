package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    private Map<Long, SectionResponse> sections;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations, Map<Long, SectionResponse> sections, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.sections = sections;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId()
            , line.getName()
            , line.getColor()
            , line.getStations()
                .stream()
                .map(StationResponse::of)
                .collect(Collectors.toList())
            , line.getSections()
                .stream()
                .collect(Collectors.toMap(
                    e -> e.getUpStation().getId(),
                    e -> SectionResponse.of(e.getUpStation(), e.getDownStation(), e.getDistance()))
                )
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

    public Map<Long, SectionResponse> getSections() {
        return sections;
    }

    public SectionResponse getSectionFromUpStationId(Long upStationId) {
        return sections.get(upStationId);
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
