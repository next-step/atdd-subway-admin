package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.exception.ErrorStatus;
import nextstep.subway.exception.IllegalRequestBody;

public class LineSaveRequest {
    private static final Long MIN_DISTANCE = 0L;
    private static final Long MIN_STATION_ID = 1L;
    private String name;
    private String color;
    private Long distance;
    private Long upStationId;
    private Long downStationId;

    public LineSaveRequest(String name, String color, Long distance, Long upStationId, Long downStationId) {
        validateName(name);
        validateColor(color);
        validateDistacne(distance);
        validateStationId(upStationId);
        validateStationId(downStationId);
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }


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

    private void validateDistacne(Long distance) {
        if (distance <= MIN_DISTANCE) {
            throw new IllegalRequestBody(ErrorStatus.BAD_REQUEST_DISTANCE.getMessage());
        }
    }

    private void validateStationId(Long upStationId) {
        if (upStationId < MIN_STATION_ID) {
            throw new IllegalRequestBody(ErrorStatus.BAD_REQUEST_STATION_ID.getMessage());
        }
    }
}
