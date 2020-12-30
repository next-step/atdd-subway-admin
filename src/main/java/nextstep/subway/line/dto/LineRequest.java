package nextstep.subway.line.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import nextstep.subway.line.domain.Line;

public class LineRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String color;

    @NotNull
    @Positive
    private Long upStationId;

    @NotNull
    @Positive
    private Long downStationId;

    @Positive
    private int distance;

    private LineRequest() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Line toLine() {
        return new Line(name, color);
    }
}
