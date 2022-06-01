package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.List;

public class LineRequest {
    private String name;
    private String color;
    private List<Station> lastStations;

    public String getName() {
        return name;
    }
    public String getColor() {
        return color;
    }

    public Line toLine() {
        return new Line(name, color, lastStations);
    }
}
