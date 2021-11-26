package nextstep.subway.line.dto;

public class LineStationRequest {

    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineStationRequest() {
    }

    public LineStationRequest(Long upStationId, Long downStationId, int distance) {
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

    public int getDistance() {
        return distance;
    }
}
