package nextstep.subway.section.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.section.domain.Section;

@Builder
@Getter
public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section toSection() {
        return new Section(upStationId, downStationId, distance);
    }
}
