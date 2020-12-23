package nextstep.subway.line.dto;

import javax.validation.constraints.NotNull;

public class SectionRequest {
    @NotNull
    private final Long downStationId;
    @NotNull
    private final Long upStationId;
    @NotNull
    private final Long distance;

    public SectionRequest(final Long upStationId, final Long downStationId, final Long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDistance() {
        return distance;
    }
}
