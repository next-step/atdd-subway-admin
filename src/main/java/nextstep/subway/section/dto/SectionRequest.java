package nextstep.subway.section.dto;

import nextstep.subway.station.dto.StationResponse;

public class SectionRequest {

    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public SectionRequest(Long upStationId, Long downStationId, Long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

}
