package nextstep.subway.line.dto;

import nextstep.subway.exception.InputDataErrorCode;
import nextstep.subway.exception.InputDataErrorException;
import nextstep.subway.line.domain.Distance;

public class SectionRequest {
    private Long upStationId;         // 상행역 아이디
    private Long downStationId;       // 하행역 아이디
    private Distance distance;             // 거리

    private SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, Distance distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public void checkValidRequestValue() {
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

    public Distance getDistance() {
        return distance;
    }
}
