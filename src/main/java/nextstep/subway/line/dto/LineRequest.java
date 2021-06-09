package nextstep.subway.line.dto;

import static java.util.Objects.*;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

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

    public void validateSaveRequest() {
        if (isNull(name) || isNull(color) || isNull(upStationId) || isNull(downStationId) || distance <= 0) {
            throw new IllegalArgumentException("노선 저장요청이 유효하지 않습니다.");
        }
    }
}
