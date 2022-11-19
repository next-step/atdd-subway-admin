package nextstep.subway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SectionRequest {
    private Long upStationId;         // 상행역 아이디
    private Long downStationId;       // 하행역 아이디
    private int distance;             // 거리
}
