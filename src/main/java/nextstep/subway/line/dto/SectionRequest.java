package nextstep.subway.line.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SectionRequest {
    private Long id;
    private Long upStationId;
    private Long downStationId;
    private Long distance;
}
