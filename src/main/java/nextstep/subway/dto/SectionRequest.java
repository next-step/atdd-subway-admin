package nextstep.subway.dto;

public class SectionRequest {

    private final Long downStationId;
    private final Long upStationId;
    private final int distance;

    public SectionRequest(Long downStationId, Long upStationId, int distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }
}
