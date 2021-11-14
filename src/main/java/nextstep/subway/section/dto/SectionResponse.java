package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

public class SectionResponse {

    private StationResponse upStationResponse;
    private StationResponse downStationResponse;
    private int distance;

    protected SectionResponse() {

    }

    public SectionResponse(Station upStation, Station downStation, int distance) {
        this.upStationResponse = StationResponse.of(upStation);
        this.downStationResponse = StationResponse.of(downStation);
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getUpStation(), section.getDownStation(), section.getDistance());
    }
}
