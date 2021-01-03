package nextstep.subway.line.dto;

import nextstep.subway.station.dto.StationResponse;

public class SectionResponse {
    private StationResponse upStation;
    private StationResponse downStation;
    private int distance;

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
