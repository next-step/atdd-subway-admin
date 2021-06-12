package nextstep.subway.section.dto;

public class SectionRequest {
    private long upStationId;         // 상행역 아이디
    private long downStationId;       // 하행역 아이디
    private int distance;             // 거리

    private SectionRequest(){}

    public SectionRequest(long upStationId, long downStationId, int distance) {
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

    public int getDistance() {
        return distance;
    }
}
