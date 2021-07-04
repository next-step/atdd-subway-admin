package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

public class SectionRequest {
    private int distance;
    private Long upStationId;
    private Long downStationId;

    public SectionRequest() {}

    public SectionRequest(int distance) {
        this.distance = distance;
    }

    public SectionRequest(int distance, Long upStationId, Long downStationId) {
        this.distance = distance;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }
    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Section toSection() {
        return new Section(distance, upStationId, downStationId);
    }
}
