package nextstep.subway.section.dto;

public class SectionRequest {

	private Long upStationId;

	private Long downStationId;

	private int distance;

	public Long getUpStationId() {
		return upStationId;
	}

	public void setUpStationId(Long upStationId) {
		this.upStationId = upStationId;
	}

	public Long getDownStationId() {
		return downStationId;
	}

	public void setDownStationId(Long downStationId) {
		this.downStationId = downStationId;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
}
