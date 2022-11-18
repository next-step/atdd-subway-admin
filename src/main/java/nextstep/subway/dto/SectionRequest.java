package nextstep.subway.dto;

public class SectionRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    private SectionRequest() {}

    public SectionRequest(Long upStationId,
                          Long downStationId,
                          int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public boolean hasNullValue() {
        return upStationId == null || downStationId == null;
    }

    public int getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

}
