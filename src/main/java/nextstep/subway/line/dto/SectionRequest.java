package nextstep.subway.line.dto;

public class SectionRequest {
    private Long upStation;
    private Long downStation;
    private int distance;

    public SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStation = upStationId;
        this.downStation = downStationId;
        this.distance = distance;
    }

    public Long getUpStation() {
        return upStation;
    }

    public Long getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }
}
