package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineUpdateRequest {
    private String name;
    private String color;

    public void updateLine(Line line) {
        line.updateLine(Line.of(name, color));
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
