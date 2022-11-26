package nextstep.subway.dto;

import nextstep.subway.domain.station.Station;

public class SectionResponse {

    private final StationResponse upStation;
    private final StationResponse downStation;
    private final int distance;

    public SectionResponse(StationResponse upStation, StationResponse downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static SectionResponse of(Station upStation, Station downStation, int distance) {
        return new SectionResponse(
                StationResponse.of(upStation),
                StationResponse.of(downStation),
                distance
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
