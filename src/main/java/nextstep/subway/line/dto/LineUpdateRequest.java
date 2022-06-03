package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

public class LineUpdateRequest {
    private String name;
    private String color;

    private LineUpdateRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static LineUpdateRequest of(String name, String color) {
        return new LineUpdateRequest(name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
