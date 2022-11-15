package nextstep.subway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SectionRequest {
    private Long upStationId;         // 상행역 아이디
    private Long downStationId;       // 하행역 아이디
    private int distance;             // 거리

    public Section toSection(Station upStation, Station downStation, Line line) {
        return new Section(upStation, downStation, line, distance);
    }
}
