package nextstep.subway.line.dto;


import nextstep.subway.line.domain.Distance;

import javax.validation.constraints.NotNull;

public class AddSectionRequest {

    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @NotNull
    private int distance;

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Distance toDistance() {
        return new Distance(distance);
    }

    public void setUpStationId(Long upStationId) {
        this.upStationId = upStationId;
    }

    public void setDownStationId(Long downStationId) {
        this.downStationId = downStationId;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}

