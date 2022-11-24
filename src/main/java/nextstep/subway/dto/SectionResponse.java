package nextstep.subway.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionResponse {
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionResponse(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        Long lineId = section.getLineId();
        Long upStationId = section.getUpStationId();
        Long downStationId = section.getDownStationId();
        int distance = section.getDistance();
        return new SectionResponse(lineId, upStationId, downStationId, distance);
    }

    public Long getLineId() {
        return lineId;
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

    public boolean findSpecificSection(StationResponse upStation, StationResponse downStation) {
        if (this.upStationId == upStation.getId() && this.downStationId == downStation.getId()) {
            return true;
        }
        return false;
    }
}
