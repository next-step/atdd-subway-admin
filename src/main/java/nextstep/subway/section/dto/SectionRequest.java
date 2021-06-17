package nextstep.subway.section.dto;

import java.util.Arrays;
import java.util.List;

public class SectionRequest {
	private Long upStationId;
	private Long downStationId;
	private int distance;

	public List<Long> getStationIds() {
		return Arrays.asList(upStationId, downStationId);
	}

	public Integer getDistance() {
		return distance;
	}

	public Long getUpStationId() {
		return upStationId;
	}

	public Long getDownStationId() {
		return downStationId;
	}
}
