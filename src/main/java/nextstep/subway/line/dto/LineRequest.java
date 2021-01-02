package nextstep.subway.line.dto;

import lombok.Builder;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import java.util.Objects;

public class LineRequest {
    private static final String NOT_FOUND_VALUE_ERROR_MESSAGE = "라인 생성시 상행역과 하행역은 필수 입력값 입니다.";
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    protected LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    @Builder
    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        validateStationId(upStationId, downStationId);
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toLine() {
        return new Line(name, color);
    }

    public Line toLine(Station upStation, Station downStation) {
        return new Line(name, color, upStation, downStation, distance);
    }

    private void validateStationId(Long upStationId, Long downStationId) {
        if (Objects.isNull(upStationId) && Objects.isNull(downStationId)) {
            throw new IllegalArgumentException(NOT_FOUND_VALUE_ERROR_MESSAGE);
        }
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
}
