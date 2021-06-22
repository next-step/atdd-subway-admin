package nextstep.subway.line.dto;

public class LineStationCreateRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineStationCreateRequest(long upStationId, long downStationId, int distance){
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
