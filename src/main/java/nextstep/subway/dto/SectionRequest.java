package nextstep.subway.dto;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public SectionRequest(Long upStationId, Long downStationId, Long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "{" +
                "\"downStationId\":\"" + downStationId +
                ", \"upStationId\":\"" + upStationId +
                ", \"distance\":\"" + distance +
                '}';
    }
}
