package nextstep.subway.line.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import nextstep.subway.common.domain.Name;
import nextstep.subway.line.domain.Color;

public class LineCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String color;

    @Valid
    @JsonUnwrapped
    private SectionRequest section;

    private LineCreateRequest() {
    }

    public LineCreateRequest(String name, String color, SectionRequest section) {
        this.name = name;
        this.color = color;
        this.section = section;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public SectionRequest getSection() {
        return section;
    }

    public Name name() {
        return Name.from(name);
    }

    public Color color() {
        return Color.from(color);
    }
}
