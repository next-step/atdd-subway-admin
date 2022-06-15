package nextstep.subway.domain;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LineColor {
    private static final String ERROR_MESSAGE_EMPTY_COLOR = "지하철노선 색상은 비어있을 수 없습니다.";

    @Column
    private String color;

    protected LineColor() {
    }

    public LineColor(String color) {
        validate(color);
        this.color = color;
    }

    private void validate(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_EMPTY_COLOR);
        }
    }

    public String getColor() {
        return color;
    }
}
