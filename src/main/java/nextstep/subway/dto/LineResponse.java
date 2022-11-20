package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Sections;

import java.util.HashSet;
import java.util.Set;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private Set<StationResponse> stations = new HashSet<>();

    private LineResponse() {}

    public LineResponse(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = sections.toStationSet();
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

    public Set<StationResponse> getStations() {
        return stations;
    }

}
