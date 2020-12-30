package nextstep.subway.line.dto;

import javax.validation.constraints.NotBlank;

/**
 * @author : leesangbae
 * @project : subway
 * @since : 2020-12-28
 */
public class LineUpdateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String color;

    private LineUpdateRequest() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
