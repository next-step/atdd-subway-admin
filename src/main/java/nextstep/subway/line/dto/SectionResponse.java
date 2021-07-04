package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionDistance;
import nextstep.subway.station.domain.Station;

public class SectionResponse {
    private Station upStation;
    private Station downStation;
    private Integer distance;

    public SectionResponse(Station upStation, Station downStation, SectionDistance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance.getDistance();
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
    }
}
