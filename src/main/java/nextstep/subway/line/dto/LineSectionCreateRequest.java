package nextstep.subway.line.dto;

public class LineSectionCreateRequest {
    private Long downStationId;
    private Long upStationId;
    private int distance;

    public LineSectionCreateRequest() {
    }


    public LineSectionCreateRequest(Long downStationId, Long upStationId, int distance) {
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
