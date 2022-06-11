package nextstep.subway.dto;


public class SectionRequest {
    private Long upStationId;

    private Long downStationId;

    private int distance;

    protected SectionRequest() {
    }

    protected SectionRequest(final Long upStationId, final Long downStationId, final int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionRequest of(final Long upStationId, final Long downStationId, final int distance) {
        return new SectionRequest(upStationId, downStationId, distance);
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
