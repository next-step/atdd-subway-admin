package nextstep.subway.line.dto;

public class LineSectionCreateRequest {
    private Long downStationId;
    private Long upStationId;
    private int distance;

    private LineSectionCreateRequest() {
    }
    public LineSectionCreateRequest(Long upStationId, Long downStationId,  int distance) {
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

    public int getDistance() {
        return distance;
    }
}
