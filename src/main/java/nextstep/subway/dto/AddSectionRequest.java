package nextstep.subway.dto;

public class AddSectionRequest {
    private long upStationId;
    private long downStationId;
    private long distance;

    public AddSectionRequest() {
    }

    public AddSectionRequest(long upStationId, long downStationId, long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public void setUpStationId(long upStationId) {
        this.upStationId = upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public void setDownStationId(long downStationId) {
        this.downStationId = downStationId;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }
}
