package nextstep.subway.dto;


import nextstep.subway.domain.Line;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;


public class LineSaveRequest {
    @NotBlank(message = "name is not null or blank")
    private String name;
    @NotBlank(message = "color is not null or blank")
    private String color;
    @Min(value = 1, message = "distance over than 0")
    private Long distance;
    @Min(value = 1, message = "upStationId over than 0")
    private Long upStationId;
    @Min(value = 1, message = "downStationId over than 0")
    private Long downStationId;

    public Line toLine() {
        return new Line(name, color, distance);
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getDistance() {
        return distance;
    }
}
