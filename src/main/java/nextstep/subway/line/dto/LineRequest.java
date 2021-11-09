package nextstep.subway.line.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

public class LineRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String color;

    @Valid
    @JsonUnwrapped
    private SectionRequest section;

    private LineRequest() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return section.getUpStationId();
    }

    public Long getDownStationId() {
        return section.getDownStationId();
    }

    public Integer getDistance() {
        return section.getDistance();
    }

    public SectionRequest getSection() {
        return section;
    }
}
