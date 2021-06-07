package nextstep.subway.section.dto;

public class SectionRequest {

    private final Long upStationId;         // 상행역 아이디
    private final Long downStationId;       // 하행역 아이디
    private final int distance;             // 거리

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
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
