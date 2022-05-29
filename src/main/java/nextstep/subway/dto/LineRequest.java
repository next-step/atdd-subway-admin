package nextstep.subway.dto;

public class LineRequest {
    private String name;

    private String color;

    private Long upStationId;

    private Long downStationId;

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
}
