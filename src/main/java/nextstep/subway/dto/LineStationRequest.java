package nextstep.subway.dto;

public class LineStationRequest {
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public LineStationRequest() {
    }

    public LineStationRequest(Long upStationId, Long downStationId, Long distance) {
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
