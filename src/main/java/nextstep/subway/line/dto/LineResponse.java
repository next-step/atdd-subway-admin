package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<Station> stations;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<Station> stations, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineResponse of(Line line) {
        List<Station> stations = extractStations(line);

        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations,
                line.getCreatedDate(), line.getModifiedDate());
    }

    private static List<Station> extractStations(Line line) {
        List<Station> upStations = extractUpStations(line);
        List<Station> downStations = extractDownStations(line);

        upStations.addAll(downStations);

        return upStations;
    }

    private static List<Station> extractDownStations(Line line) {
        return line.getSections().stream()
                    .map(Section::getDownStation)
                    .collect(Collectors.toList());
    }

    private static List<Station> extractUpStations(Line line) {
        return line.getSections().stream()
                    .map(Section::getUpStation)
                    .collect(Collectors.toList());
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

    public List<Station> getStations() {
        return stations;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
