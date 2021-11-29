package nextstep.subway.line.dto;

public class SectionAddRequest {

    private long upStationId;
    private long downStationId;
    private int distance;

    public SectionAddRequest() {

    }

    private SectionAddRequest(long upStationId, long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionAddRequest of(long upStationId, long downStationId, int distance) {
        return new SectionAddRequest(upStationId, downStationId, distance);
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
