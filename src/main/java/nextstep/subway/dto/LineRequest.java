package nextstep.subway.dto;

import java.util.Objects;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

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

    public Integer getDistance() {
        return distance;
    }

    public boolean isSameStations() {
        return Objects.equals(this.downStationId, this.upStationId);
    }
}
