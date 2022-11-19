package nextstep.subway.dto;

import nextstep.subway.domain.Section;

public class SectionResponse {

    private int distance;
    private StationResponse upStation;
    private StationResponse downStation;

    public SectionResponse(int distance, StationResponse upStation, StationResponse downStation) {
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getDistance(), StationResponse.of(section.getUpStation()),
                StationResponse.of(section.getDownStation()));
    }

    public int getDistance() {
        return distance;
    }

    public StationResponse getUpStation() {
        return upStation;
    }

    public StationResponse getDownStation() {
        return downStation;
    }
}
