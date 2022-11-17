package nextstep.subway.dto;

import nextstep.subway.domain.Color;
import nextstep.subway.domain.Name;

public class LineUpdateRequest {

    private final String name;
    private final String color;

    public LineUpdateRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Name getName() {
        return new Name(name);
    }

    public Color getColor() {
        return new Color(color);
    }
}
