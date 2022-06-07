package nextstep.subway.dto;

public class SectionRequest {

    private Long upStationId;         // 상행역 아이디
    private Long downStationId;       // 하행역 아이디
    private Long distance;

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public long getDistance() {
        return distance;
    }
}
