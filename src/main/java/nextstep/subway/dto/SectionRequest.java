package nextstep.subway.dto;


public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public SectionRequest() {

    }

    public SectionRequest(Long upStationId, Long downStationId, Long distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDistance() {
        return distance;
    }
}
