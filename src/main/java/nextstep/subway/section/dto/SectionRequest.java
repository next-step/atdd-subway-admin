package nextstep.subway.section.dto;

public class SectionRequest {

    private final Long upStationId;
    private final Long downStationId;
    private final Integer distance;

    public SectionRequest(final Long upStationId, final Long downStationId, final Integer distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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
