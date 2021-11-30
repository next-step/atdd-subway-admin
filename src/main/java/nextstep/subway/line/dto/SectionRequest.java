package nextstep.subway.line.dto;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    protected SectionRequest() {
    }

    private SectionRequest(Long upStationId, Long downStationId, Integer distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionRequest of(Long upStationId, Long downStationId, Integer distance) {
        return new SectionRequest(upStationId, downStationId, distance);
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }
}
