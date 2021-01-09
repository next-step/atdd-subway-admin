package nextstep.subway.section.dto;

import nextstep.subway.advice.exception.SectionBadRequestException;

public class SectionRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        checkDuplicationId(upStationId, downStationId);
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

    @Override
    public String toString() {
        return "SectionRequest{" +
                "upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }

    private void checkDuplicationId(Long upStationId, Long downStationId) {
        if (upStationId == downStationId) {
            throw new SectionBadRequestException(upStationId, downStationId);
        }
    }

}
