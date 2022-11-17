package nextstep.subway.dto;

import nextstep.subway.exception.ErrorStatus;
import nextstep.subway.exception.IllegalRequestBody;

public class LineUpdateRequest {
    private String name;
    private String color;

    public LineUpdateRequest(String name, String color) {
        validateName(name);
        validateColor(color);
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    private void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalRequestBody(ErrorStatus.BAD_REQUEST_NAME.getMessage());
        }
    }

    private void validateColor(String color) {
        if (color == null || color.isEmpty()) {
            throw new IllegalRequestBody(ErrorStatus.BAD_REQUEST_COLOR.getMessage());
        }
    }
}
