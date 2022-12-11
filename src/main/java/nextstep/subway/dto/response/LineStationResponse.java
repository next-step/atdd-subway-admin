package nextstep.subway.dto.response;

import nextstep.subway.domain.line.LineStation;

public class LineStationResponse {
    private Long id;
    private Long upStationId;
    private Long downStationId;

    private int distance;

    protected LineStationResponse() {}

    public LineStationResponse(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId  = downStationId;
        this.distance = distance;
    }

    public LineStationResponse(Long id, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static LineStationResponse of(LineStation lineStation) {
        return new LineStationResponse(lineStation.getId(), lineStation.getUpStationId(), lineStation.getDownStationId(), lineStation.getDistance());
    }

    public Long getId() {
        return id;
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
