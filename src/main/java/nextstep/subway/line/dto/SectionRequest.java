package nextstep.subway.line.dto;

public class SectionRequest {
	private Long upStationId;         // 상행역 아이디
	private Long downStationId;       // 하행역 아이디
	private int distance;             // 거리

	private SectionRequest() {
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
