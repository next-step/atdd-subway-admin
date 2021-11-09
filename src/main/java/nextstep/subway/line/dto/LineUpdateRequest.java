package nextstep.subway.line.dto;

import javax.validation.constraints.NotBlank;
import nextstep.subway.common.domain.Name;
import nextstep.subway.line.domain.Color;

public class LineUpdateRequest {

    @NotBlank(message = "노선 이름은 비어있을 수 없습니다.")
    private String name;

    @NotBlank(message = "노선의 색상은 비어있을 수 없습니다.")
    private String color;

    private LineUpdateRequest() {
    }

    public LineUpdateRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Name toName() {
        return Name.from(name);
    }

    public Color toColor() {
        return Color.from(name);
    }

}
