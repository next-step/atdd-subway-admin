package nextstep.subway.line.dto;

import nextstep.subway.common.exception.DuplicateParameterException;
import nextstep.subway.line.domain.Line;

import java.util.Objects;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    private LineRequest() {
    }

    private LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        if (Objects.equals(upStationId, downStationId)) {
            throw new DuplicateParameterException("상행, 하행역은 중복될 수 없습니다.");
        }
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static LineRequest of(String name, String color, Long upStationId, Long downStationId, int distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
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
