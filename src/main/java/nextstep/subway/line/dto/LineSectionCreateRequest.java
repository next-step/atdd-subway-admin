package nextstep.subway.line.dto;

public class LineSectionCreateRequest {
    private Long stationId;
    private Long preStationId;
    private int distance;

    public LineSectionCreateRequest() {
    }

    public LineSectionCreateRequest(Long stationId, Long preStationId, int distance) {
        this.stationId = stationId;
        this.preStationId = preStationId;
        this.distance = distance;
    }

    public Long getStationId() {
        return stationId;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public int getDistance() {
        return distance;
    }
}
