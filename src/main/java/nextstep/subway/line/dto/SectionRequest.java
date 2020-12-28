package nextstep.subway.line.dto;

public class SectionRequest {
	private Long upStationId;         // 상행역 아이디
	private Long downStationId;       // 하행역 아이디
	private int distance;             // 거리

	public SectionRequest() {
	}

	public SectionRequest(Long upStationId, Long downStationId, int distance) {
		this.upStationId = upStationId;
		this.downStationId = downStationId;
		this.distance = distance;
	}

	public Long getUpStationId() {
		return upStationId;
	}

	public Long getDownStationId() {
		return downStationId;
	}

	public int getDistance() {
		return distance;
	}
}
