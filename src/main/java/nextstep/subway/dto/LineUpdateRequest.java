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

    public Line toLine(Long distance, Station upStation, Station downStation) {
        return new Line(name, color, distance, upStation, downStation);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
