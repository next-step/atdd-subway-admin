package nextstep.subway.line.dto;

public class SectionRequestDto {
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    private SectionRequestDto() {
    }

    public SectionRequestDto(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Integer getDistance() {
        return distance;
    }
}
