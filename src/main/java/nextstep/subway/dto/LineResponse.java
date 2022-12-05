package nextstep.subway.dto;

import java.util.Arrays;
import java.util.List;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<Station> stations;

    public static LineResponse of(Line line) {
        List<Station> stations = Arrays.asList(line.getUpStation(), line.getDownStation());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
    }

    private LineResponse(Long id, String name, String color, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
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
}
