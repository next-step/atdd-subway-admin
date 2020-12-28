package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<SectionResponse> stations;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<SectionResponse> stations, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineResponse of(Line line) {
        List<SectionResponse> stationResponses = line.getLineSections().stream()
            .map(SectionResponse::of)
            .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses, line.getCreatedDate(), line.getModifiedDate());
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

    public List<SectionResponse> getStations() {
        return stations;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void initStations(List<Long> ids) {
        this.stations = ids.stream()
            .map(SectionResponse::new)
            .collect(Collectors.toList());
    }
}
