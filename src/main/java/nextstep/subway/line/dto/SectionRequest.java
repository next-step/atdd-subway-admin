package nextstep.subway.line.dto;

public class SectionRequest {
	private Long upStationId;         // 상행역 아이디
	private Long downStationId;       // 하행역 아이디
	private int distance;             // 거리

	protected SectionRequest() {
	}

	public SectionRequest(Long upStationId, Long downStationId, int distance) {
		this.upStationId = upStationId;
		this.downStationId = downStationId;
		this.distance = distance;
	}

	public void validateRequest() {
		if (this.upStationId == null && this.downStationId == null) {
			throw new IllegalArgumentException("상행역과 하행역 중 하나는 선택해야합니다.");
		}

		if (this.distance == 0) {
			throw new IllegalArgumentException("추가되는 구간의 거리는 0이 될 수 없습니다.");
		}
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
