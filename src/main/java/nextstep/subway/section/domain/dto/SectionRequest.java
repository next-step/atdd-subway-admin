package nextstep.subway.section.domain.dto;

import nextstep.subway.exception.InputDataErrorCode;
import nextstep.subway.exception.InputDataErrorException;

public class SectionRequest {
    private Long upStationId;         // 상행역 아이디
    private Long downStationId;       // 하행역 아이디
    private int distance;             // 거리

    public SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public void checkValidRequestValue() {
        if (this.distance <= 0) {
            throw new InputDataErrorException(InputDataErrorCode.DISTANCE_IS_NOT_LESS_THEN_ZERO);
        }

        if (this.upStationId == null && this.downStationId == null) {
            throw new InputDataErrorException(InputDataErrorCode.THEY_ARE_NOT_SEARCHED_STATIONS);
        }
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
