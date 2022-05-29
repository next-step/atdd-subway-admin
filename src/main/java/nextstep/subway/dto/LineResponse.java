package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, List<StationResponse> list) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = list;
    }

    public static LineResponse from(Line line) {
        Set<StationResponse> stations = new HashSet<>();
        for (Section section : line.getSections()) {
            stations.add(StationResponse.of(section.getUpStation()));
            stations.add(StationResponse.of(section.getDownStation()));
        }

        return new LineResponse(line.getId(), line.getName(), line.getColor(), new ArrayList<>(stations));
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
