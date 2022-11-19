package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Color;
import nextstep.subway.common.domain.Name;

public class LineUpdateRequest {

    private String name;
    private String color;

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Name toName() {
        return new Name(this.name);
    }

    public Color toColor() {
        return new Color(this.color);
    }
}

