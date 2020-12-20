package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LineRequest {
    @NotNull(message = "노선명 필수값입니다.")
    @NotEmpty(message = "노선명은 1글자 이상이어야 합니다.")
    private String name;
    @NotNull(message = "노선색은 필수값입니다.")
    private String color;
    @NotNull(message = "상행성역은 필수값입니다.")
    private Long upStationId;
    @NotNull(message = "하행선역은 필수값입니다.")
    private Long downStationId;
    @NotNull(message = "역간 거리는 필수값입니다.")
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
