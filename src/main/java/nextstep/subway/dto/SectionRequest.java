package nextstep.subway.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SectionRequest {

    @NotNull
    private Long upStationId;         // 상행역 아이디
    @NotNull
    private Long downStationId;       // 하행역 아이디
    @Min(1L)
    private Long distance;

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public long getDistance() {
        return distance;
    }
}
