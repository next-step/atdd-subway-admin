package nextstep.subway.utils;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class LineBuilder {
    private String lineName;
    private String lineColor;
    private Section section;

    public LineBuilder withName(String name) {
        this.lineName = name;
        return this;
    }

    public LineBuilder withColor(String color) {
        this.lineColor = color;
        return this;
    }

    public LineBuilder withSection(Section section) {
        this.section = section;
        return this;
    }

    public Line build() {
        Line line = new Line(lineName, lineColor);
        line.createSection(section);
        return line;
    }
}
