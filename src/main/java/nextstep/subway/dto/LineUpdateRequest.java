package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineUpdateRequest {
    private String name;
    private String color;

    public LineUpdateRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }


    public static LineUpdateRequest of(String name, String color) {
        return new LineUpdateRequest(name, color);
    }

    public Line toLine(Station upStation, Station downStation, int distance) {
        return new Line(name, color, upStation, downStation, distance);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
