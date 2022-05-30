package nextstep.subway.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionResponse {
    private final Long id;
    private final StationResponse upStation;
    private final StationResponse downStation;
    private final int distance;

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(), section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    public SectionResponse(Long id, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.upStation = StationResponse.of(upStation);
        this.downStation = StationResponse.of(downStation);
        this.distance = distance;
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

    @Override
    public String toString() {
        return "SectionResponse{" +
                "id=" + id +
                ", distance=" + distance +
                '}';
    }
}
