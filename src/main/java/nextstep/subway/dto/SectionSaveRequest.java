package nextstep.subway.dto;

import nextstep.subway.exception.ErrorStatus;
import nextstep.subway.exception.IllegalRequestBody;

public class SectionSaveRequest {
    private static final Long MINIMUM_DISTANCE = 0L;
    private static final Long MINIMUM_STATION_ID = 0L;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public SectionSaveRequest(Long upStationId, Long downStationId, Long distance) {
        validateDistance(distance);
        validateStaionId(upStationId, downStationId);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validateStaionId(Long upStationId, Long downStationId) {
        if (upStationId <= MINIMUM_STATION_ID || downStationId <= MINIMUM_STATION_ID) {
            throw new IllegalRequestBody(ErrorStatus.BAD_REQUEST_STATION_ID.getMessage());
        }
    }

    private void validateDistance(Long distance) {
        if (distance <= MINIMUM_DISTANCE) {
            throw new IllegalRequestBody(ErrorStatus.BAD_REQUEST_DISTANCE.getMessage());
        }
    }


    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
        return distance;
    }
}
