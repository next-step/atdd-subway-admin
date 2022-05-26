package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    protected SectionRequest() {}

    private SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionRequest of(Long upStationId, Long downStationId, int distance) {
        return new SectionRequest(upStationId, downStationId, distance);
    }

    public Long getUpStationId() {
        return this.upStationId;
    }

    public Long getDownStationId() {
        return this.downStationId;
    }

    public int getDistance() {
        return this.distance;
    }

    public Section toSection() {
        return Section.of(Station.from(this.upStationId), Station.from(this.downStationId),
            Distance.from(this.distance));
    }
}
