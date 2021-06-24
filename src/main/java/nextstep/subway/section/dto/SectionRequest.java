package nextstep.subway.section.dto;

public class SectionRequest {

    private long downStationId;
    private long upStationId;
    private long distance;

    public SectionRequest(long downStationId, long upStationId, long distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDistance() {
        return distance;
    }
}
