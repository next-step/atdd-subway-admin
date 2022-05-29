package nextstep.subway.dto;

public class SectionRequestDTO {

    private final Long upStationId;
    private final Long downStationId;
    private final Long distance;

    public SectionRequestDTO(Long upStationId, Long downStationId, Long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

}
