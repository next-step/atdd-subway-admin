package nextstep.subway.line.dto;

import javax.validation.constraints.NotNull;

public class SectionRequest {

    @NotNull
    private Long upStationId;

    @NotNull
    private Long downStationId;

    @NotNull
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
