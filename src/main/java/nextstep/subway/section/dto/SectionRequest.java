package nextstep.subway.section.dto;

public class SectionRequest {

    private long upStationId;
    private long downStationId;
    private long distance;

    public SectionRequest() {
    }

    public SectionRequest(long upStationId, long downStationId, long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
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
