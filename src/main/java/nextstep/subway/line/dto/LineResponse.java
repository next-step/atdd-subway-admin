package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.dto.StationResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;

    private String name;

    private String color;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    private List<StationResponse> stations;

    private LineResponse() {
    }

    private LineResponse(Long id, String name, String color, LocalDateTime createdDate, LocalDateTime modifiedDate, List<Section> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.stations = new ArrayList<>(toStations(sections));
    }

    private List<StationResponse> toStations(List<Section> sections) {
        List<StationResponse> stations = sections.stream().map(Section::getStation).map(StationResponse::of).collect(Collectors.toList());
        stations.add(StationResponse.of(sections.get(sections.size() - 1).getNextStation()));
        return stations;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getCreatedDate(), line.getModifiedDate(), line.getSections());
    }

    public static List<LineResponse> ofList(List<Line> lines) {
        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
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
}
