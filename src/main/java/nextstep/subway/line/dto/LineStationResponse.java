package nextstep.subway.line.dto;

public class LineStationResponse {
    private Long upStationId;
    private Long stationId;
    private Integer distance;

    public LineStationResponse(Long upStationId, Long stationId, Integer distance){
        this.upStationId = upStationId;
        this.stationId = stationId;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getStationId() {
        return stationId;
    }

    public Integer getDistance() {
        return distance;
    }
}
