package nextstep.subway.line.dto;

public class LineStationRequest {

    private Long preStationId;
    private Long nextStationId;
    private int distance;

    public LineStationRequest() {
    }

    public LineStationRequest(Long preStationId, Long nextStationId, int distance) {
        this.preStationId = preStationId;
        this.nextStationId = nextStationId;
        this.distance = distance;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Long getNextStationId() {
        return nextStationId;
    }

    public int getDistance() {
        return distance;
    }
}
