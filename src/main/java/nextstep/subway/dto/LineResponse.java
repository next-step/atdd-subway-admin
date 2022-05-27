package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<Section> sections;

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getSections());
    }

    public LineResponse() {

    }

    public LineResponse(Long id, String name, String color, List<Section> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
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
        List<Station> stations = new ArrayList<>();
        for (Section section : sections) {
            stations.addAll(section.getStations());
        }
        return stations;
    }
}
