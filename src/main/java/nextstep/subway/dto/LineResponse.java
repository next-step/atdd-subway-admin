package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Sections;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations = new ArrayList<>();

    private LineResponse() {}

    public LineResponse(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = sections.getSortList().stream()
                .map(section -> StationResponse.of(section.getStation()))
                .collect(Collectors.toList());
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getSection());
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
