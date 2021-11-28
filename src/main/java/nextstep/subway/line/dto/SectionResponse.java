package nextstep.subway.line.dto;

import nextstep.subway.station.domain.Station;

public class SectionResponse {
    Station upStation;

    Station downStation;

    int distance;

    public SectionResponse() {
    }

    public SectionResponse(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static SectionResponse of(Station upStation, Station downStation, int distance) {
        return new SectionResponse(upStation, downStation, distance);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }
}
