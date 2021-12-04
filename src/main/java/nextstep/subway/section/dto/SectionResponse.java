package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.dto.StationResponse;

public class SectionResponse {

    private StationResponse upStation;
    private StationResponse downStation;
    private int distance;

    public SectionResponse() {
    }

    public SectionResponse(StationResponse upStation, StationResponse downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(
                StationResponse.of(section.getUpStation()),
                StationResponse.of(section.getDownStation()),
                section.getDistance().getDistance()
        );
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
}
