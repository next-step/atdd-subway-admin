package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import javax.validation.constraints.NotNull;

public class LineRequest {
    private String name;
    private String color;
    @NotNull(message = "상행성역은 필수값입니다.")
    private Long upStationId;
    @NotNull(message = "하행선역은 필수값입니다.")
    private Long downStationId;
    private Long distance;

    protected LineRequest() {
        // for serialize
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, Long distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public Long getDistance() {
        return distance;
    }

    public Line toLine() {
        return new Line(name, color);
    }
}
