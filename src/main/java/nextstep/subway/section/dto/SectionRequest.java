package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Distance;

public class SectionRequest {
	private Long upStationId;
	private Long downStationId;
	private Distance distance;

	protected SectionRequest() {
	}

	public SectionRequest(Long upStationId, Long downStationId, int distance) {
		this.upStationId = upStationId;
		this.downStationId = downStationId;
		this.distance = new Distance(distance);
	}

	public Long getUpStationId() {
		return upStationId;
	}

	public Long getDownStationId() {
		return downStationId;
	}

	public int getDistance() {
		return distance.getDistance();
	}
}
