package nextstep.subway.line.dto;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionRequest of(Long upStationId, Long downStationId, int distance ) {
        return new SectionRequest(upStationId, downStationId, distance);
    }

    public Long getUpStationId() {
        return this.upStationId;
    }

    public Long getDownStationId() {
        return this.downStationId;
    }

    public int getDistance() {
        return this.distance;
    }
}
