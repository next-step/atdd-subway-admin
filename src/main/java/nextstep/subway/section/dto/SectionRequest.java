package nextstep.subway.section.dto;

import lombok.Getter;

@Getter
public class SectionRequest {
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
