package nextstep.subway.dto.line.section;

public class SectionCreateRequest {

	private Long upStationId;
	private Long downStationId;
	private int distance;

	private SectionCreateRequest() {
	}

	public SectionCreateRequest(Long upStationId, Long downStationId, int distance) {
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
