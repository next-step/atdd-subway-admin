package nextstep.subway.section.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.dto.StationResponse;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SectionResponse {

    private StationResponse upStation;
    private StationResponse downStation;
    private int distance;

    public static SectionResponse of(Section section) {
        return new SectionResponse(
                StationResponse.of(section.getUpStation()),
                StationResponse.of(section.getDownStation()),
                section.getDistance());
    }
}
