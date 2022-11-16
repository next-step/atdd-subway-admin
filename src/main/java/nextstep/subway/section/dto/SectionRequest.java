package nextstep.subway.section.dto;


import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionRequest {
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    protected SectionRequest() {
    }

    public SectionRequest(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getLineId() {
        return lineId;
    }

    public Section toSection(Station upStation, Station downStation) {
        return new Section(upStation, downStation, distance);
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

}
