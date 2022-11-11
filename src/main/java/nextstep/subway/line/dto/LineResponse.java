package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.dto.StationResponse;

import java.util.ArrayList;
import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;

    private List<StationResponse> stations;

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getSections());
    }

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<Section> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = toStationResponses(sections);
    }

    private List<StationResponse> toStationResponses(List<Section> sections) {
        List<StationResponse> stationResponses = new ArrayList<>();
        sections.forEach((section) -> {
            stationResponses.add(StationResponse.of(section.getUpStation()));
            stationResponses.add(StationResponse.of(section.getDownStation()));
        });
        return stationResponses;
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
}
