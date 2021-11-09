package nextstep.subway.line.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SectionRequest {

    @NotNull(message = "상행선 id는 반드시 존재해야 합니다.")
    private Long upStationId;

    @NotNull(message = "하행선 id는 반드시 존재해야 합니다.")
    private Long downStationId;

    @Min(value = 1, message = "구간 거리는 반드시 1이상 이어야 합니다.")
    @NotNull(message = "구간 거리는 반드시 존재해야 합니다.")
    private Integer distance;

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Integer getDistance() {
        return distance;
    }
}
