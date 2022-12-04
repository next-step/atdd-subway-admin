package nextstep.subway.dto.request;

import nextstep.subway.domain.line.LineStation;

public class LineSectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    protected LineSectionRequest() {}

    public LineSectionRequest(Long upStationId, Long downStationId, int distance) {
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

    public LineStation toLineStation(LineSectionRequest lineSectionRequest) {
        return new LineStation(lineSectionRequest.upStationId, lineSectionRequest.getDownStationId(), lineSectionRequest.getDistance());
    }
}
