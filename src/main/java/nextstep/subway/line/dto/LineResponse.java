package nextstep.subway.line.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<StationResponse> stationList = new ArrayList<>();

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, LocalDateTime createdDate, LocalDateTime modifiedDate, Sections stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.stationList = toStationResponse(stations).stream()
                .map(this::stationResponse)
                .collect(Collectors.toList());
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getCreatedDate(), line.getModifiedDate(), line.stations());
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

    public List<StationResponse> getStationList() {
        return stationList;
    }

    private StationResponse stationResponse(Station station) {
        return StationResponse.of(station);
    }

    private List<Station> toStationResponse(Sections sections) {
        return getOrderedStations(sections);
    }

    private List<Station> getOrderedStations(Sections sections) {
        List<Station> result = new LinkedList<>();

        result.add(sections.getFirstSection().getUpStation());
        result.add(sections.getFirstSection().getDownStation());

        addUpStation(sections, result, result.get(0));
        addDownStation(sections, result, result.get(result.size() - 1));

        return result;
    }

    private void addUpStation(Sections sections, List<Station> result, Station findStation) {
        while (sections.hasDownStation(findStation)) {
            Section findSection = sections.findSectionByDownStation(findStation);
            result.add(0, findSection.getUpStation());
            findStation = findSection.getUpStation();
        }
    }

    private void addDownStation(Sections sections, List<Station> result, Station findStation) {
        while (sections.hasUpStation(findStation)) {
            Section findSection = sections.findSectionByUpStation(findStation);

            result.add(findSection.getDownStation());
            findStation = findSection.getDownStation();
        }
    }
}
