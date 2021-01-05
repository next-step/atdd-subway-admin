package nextstep.subway.section.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SectionRequest {
	@NotNull
	private final Long upStationId;
	@NotNull
	private final Long downStationId;
	@Min(1)
	private final int distance;
}
