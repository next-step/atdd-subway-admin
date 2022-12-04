package nextstep.subway.dto;

public class SectionRequest {
    private long upStationId;
    private long downStationId;
    private long distance;

    protected SectionRequest() {}

    public SectionRequest(long upStationId, long donwStationId, long distance) {
        this.upStationId = upStationId;
        this.downStationId = donwStationId;
        this.distance = distance;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public long getDistance() {
        return distance;
    }
}
