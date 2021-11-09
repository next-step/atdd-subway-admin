package nextstep.subway.line.dto;

import javax.validation.constraints.NotNull;
import nextstep.subway.line.domain.Distance;

public class SectionRequest {

    @NotNull
    private Long upStationId;

    @NotNull
    private Long downStationId;

    @NotNull
    private Integer distance;

    private SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, Integer distance) {
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

    public Integer getDistance() {
        return distance;
    }

    public Distance distance() {
        return Distance.from(distance);
    }
}
