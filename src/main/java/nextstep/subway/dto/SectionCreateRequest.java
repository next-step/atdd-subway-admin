package nextstep.subway.dto;

public class SectionCreateRequest {
    private long downStationId;
    private long upStationId;
    private int distance;

    public SectionCreateRequest(long downStationId, long updateStationId, int distance) {
        this.downStationId = downStationId;
        this.upStationId = updateStationId;
        this.distance = distance;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }
}
