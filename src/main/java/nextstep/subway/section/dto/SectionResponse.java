package nextstep.subway.section.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SectionResponse {
    private Station upStation;
    private Station downStation;
    private Long distance;

    public SectionResponse(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance.get();
    }

    public static SectionResponse of(Section section){
        return new SectionResponse(section.getUpStation(), section.getDownStation(), section.getDistance());
    }
}
