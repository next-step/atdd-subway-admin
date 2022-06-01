package nextstep.subway.dto;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Section;

public class SectionResponse {
    private StationResponse upStation;
    private StationResponse downStation;
    private int distance;

    private SectionResponse() {

    }

    public SectionResponse(StationResponse upStation, StationResponse downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance.value();
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(StationResponse.of(section.getUpStation()),
                StationResponse.of(section.getDownStation()),
                section.getDistance());
    }

    public StationResponse getUpStation() {
        return upStation;
    }

    public void setUpStation(StationResponse upStation) {
        this.upStation = upStation;
    }

    public StationResponse getDownStation() {
        return downStation;
    }

    public void setDownStation(StationResponse downStation) {
        this.downStation = downStation;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
