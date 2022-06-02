package nextstep.subway.line.dto;

public class LineRequest {

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

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

    private LineRequest(
        String name,
        String color,
        Long upStationId,
        Long downStationId,
        Long distance
    ) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static LineRequest of(
        String name,
        String color,
        Long upStationId,
        Long downStationId,
        Long distance
    ) {
        return new LineRequest(
            name,
            color,
            upStationId,
            downStationId,
            distance
        );
    }
}
