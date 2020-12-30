package nextstep.subway.section.dto;

import lombok.*;
import nextstep.subway.section.domain.Section;
import org.springframework.stereotype.Service;

@Builder
@Getter
@AllArgsConstructor
public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section toSection() {
        return new Section(upStationId, downStationId, distance);
    }
}
