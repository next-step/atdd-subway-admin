package nextstep.subway.dto;

public class LineStationRequest implements CreateLineStationRequest {

    private final Long distance;
    private final Long upStationId;
    private final Long downStationId;

    public LineStationRequest(Long distance, Long upStationId, Long downStationId) {
        this.distance = distance;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Long getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }
}
