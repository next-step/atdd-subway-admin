package nextstep.subway.line.dto;

public class AddSectionRequest {
	public final Long upStationId;
	public final Long downStationId;
	public final int distance;

	public AddSectionRequest(Long upStationId, Long downStationId, int distance) {
		this.upStationId = upStationId;
		this.downStationId = downStationId;
		this.distance = distance;
	}
}
