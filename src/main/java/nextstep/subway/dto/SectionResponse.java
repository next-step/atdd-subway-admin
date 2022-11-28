package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionResponse {
    private Long lineId;
    private StationResponse upStation;
    private StationResponse downStation;
    private int distance;

    public SectionResponse(Long lineId, StationResponse upStation, StationResponse downStation, int distance) {
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        Long lineId = section.getLineId();
        StationResponse upStation = StationResponse.of(section.getUpStation());
        StationResponse downStation = StationResponse.of(section.getDownStation());
        int distance = section.getDistance();
        return new SectionResponse(lineId, upStation, downStation, distance);
    }

    public Long getLineId() {
        return lineId;
    }

    public StationResponse getUpStation() {
        return upStation;
    }

    public StationResponse getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public boolean findSpecificSection(StationResponse upStation, StationResponse downStation) {
        if (this.upStation.getId() == upStation.getId() && this.downStation.getId() == downStation.getId()) {
            return true;
        }

        return false;
    }
}
