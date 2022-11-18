package nextstep.subway.dto;

import nextstep.subway.domain.Color;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Name;
import nextstep.subway.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class LineResponse {
    private final Long id;
    private final Name name;
    private final Color color;
    private final List<Station> stations;

    public LineResponse(Long id, Name name, Color color, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        List<Station> stations = new ArrayList<>();
        stations.add(line.getUpStation());
        stations.add(line.getDownStation());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getName();
    }

    public String getColor() {
        return color.getColor();
    }
}
