package nextstep.subway.domain.raw;

import nextstep.subway.dto.LineRequest;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import static nextstep.subway.constant.Message.NOT_VALID_EMPTY;
import static nextstep.subway.constant.Message.NOT_VALID_UNDER_ZERO_DISTANCE;

@Embeddable
public class Color {
    @Column(nullable = false)
    private String color;

    protected Color() {
    }

    public Color(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public Color from(LineRequest updateRequest) {
        if (updateRequest.getColor().isEmpty() || updateRequest.getColor() == "") {
            throw new IllegalArgumentException(NOT_VALID_EMPTY);
        }
        return new Color(updateRequest.getColor());
    }
}
