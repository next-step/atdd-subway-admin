package nextstep.subway.line.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SectionResponse {
    private StationResponse upStation;
    private StationResponse downStation;
    private Long distance;

    public SectionResponse(Station upStation, Station downStation, Distance distance) {
        this.upStation = StationResponse.of(upStation);
        this.downStation = StationResponse.of(downStation);
        this.distance = distance.get();
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getUpStation(), section.getDownStation(), section.getDistance());
    }
}
