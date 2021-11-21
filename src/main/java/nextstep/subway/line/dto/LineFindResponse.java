package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.dto.StationResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LineFindResponse {

    private Long id;
    private String name;
    private String color;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<StationResponse> stations = new ArrayList<>();

    public LineFindResponse() {
    }

    private LineFindResponse(Long id, String name, String color, LocalDateTime createdDate, LocalDateTime modifiedDate, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.stations = stations;
    }

    public static LineFindResponse of(Line line) {
        List<StationResponse> stations = createStations(line);
        return new LineFindResponse(line.getId(), line.getName(), line.getColor(), line.getCreatedDate(), line.getModifiedDate(), stations);
    }

    public static List<LineFindResponse> ofList(List<Line> lines) {
        List<LineFindResponse> lineFindResponses = new ArrayList<>();
        for (Line line : lines) {
            lineFindResponses.add(of(line));
        }
        return lineFindResponses;
    }

    private static List<StationResponse> createStations(Line line) {
        List<StationResponse> stations = new ArrayList<>();
        List<Section> sections = line.getSections();
        for (Section section : sections) {
            stations.add(StationResponse.of(section.getUpStation()));
        }
        stations.add(StationResponse.of(sections.get(sections.size() - 1).getDownStation()));
        return stations;
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
