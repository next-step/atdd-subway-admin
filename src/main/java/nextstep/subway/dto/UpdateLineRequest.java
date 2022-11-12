package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class UpdateLineRequest {

    private String name;
    private String color;

    public UpdateLineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void updateLine(Line line) {
        line.updateLineNameAndColor(name, color);
    }
}
