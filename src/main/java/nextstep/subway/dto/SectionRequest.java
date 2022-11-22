package nextstep.subway.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.exception.InvalidParameterException;

public class SectionRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    private SectionRequest() {}

    public SectionRequest(Long upStationId,
                          Long downStationId,
                          int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public void checkValidationParameter() {
        if (hasNullValue()) {
            throw new InvalidParameterException("상행역과 하행역의 요청값은 비어있으면 안됩니다.");
        }
    }

    public boolean hasNullValue() {
        return upStationId == null || downStationId == null;
    }

    public int getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

}
