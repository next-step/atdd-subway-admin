package nextstep.subway.dto;

public class LineRequestDTO {

    private String name;
    private String color;
    private Long upStaionId;
    private Long downStationId;
    private Long distance;

    public LineRequestDTO(String name, String color, Long upStaionId, Long downStationId, Long distance) {
        this.name = name;
        this.color = color;
        this.upStaionId = upStaionId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStaionId() {
        return upStaionId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
        return distance;
    }

}
