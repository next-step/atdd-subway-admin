package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionRequest {

    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public SectionRequest() {}

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

    public Section toSection(Station upStation, Station downStation) {
        return Section.of(upStation, downStation, this.distance);
    }

    public Integer getDistance() {
        return distance;
    }

}
