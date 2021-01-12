package nextstep.subway.section.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.section.domain.Distance;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class SectionRequest {
	private Long upStationId;
	private Long downStationId;
	private Distance distance;
}
