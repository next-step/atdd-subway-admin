package nextstep.subway.dto;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionResponse {
    private Long id;
    private StationResponse upStation;
    private StationResponse downStation;
    private Integer distance;

    public SectionResponse(Long id, Station upStation, Station downStation, Distance distance) {
        this.id = id;
        this.upStation = StationResponse.of(upStation);
        this.downStation = StationResponse.of(downStation);
        this.distance = distance.getDistance();
    }

    public Long getId() {
        return id;
    }

    public StationResponse getUpStation() {
        return upStation;
    }

    public StationResponse getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(), section.getUpStation(), section.getDownStation(), section.getDistance());
    }
}
