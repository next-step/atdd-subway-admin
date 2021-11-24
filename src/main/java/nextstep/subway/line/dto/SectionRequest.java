package nextstep.subway.line.dto;

public class SectionRequest {
	private Long downStationId;
	private Long upStationId;
	private int distance;

	public SectionRequest(Long downStationid, Long upStationId, int distance) {
		this.downStationId = downStationid;
		this.upStationId = upStationId;
		this.distance = distance;
	}

	public Long getDownStationId() {
		return downStationId;
	}

	public Long getUpStationId() {
		return upStationId;
	}

	public int getDistance() {
		return distance;
	}
}
