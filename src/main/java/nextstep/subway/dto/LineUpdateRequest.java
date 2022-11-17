package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineUpdateRequest {
    private String name;
    private String color;

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
