package nextstep.subway.line.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LineUpdateRequest {
    private final String name;
    private final String color;

    @JsonCreator
    public LineUpdateRequest(@JsonProperty("name") final String name,
                             @JsonProperty("color") final String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "LineAddRequest{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
