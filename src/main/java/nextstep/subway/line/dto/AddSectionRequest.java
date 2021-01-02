package nextstep.subway.line.dto;

public class AddSectionRequest {
	private long upStationId;
	private long downStationId;
	private int distance;

	public AddSectionRequest() {
	}

	public AddSectionRequest(long upStationId, long downStationId, int distance) {
		this.upStationId = upStationId;
		this.downStationId = downStationId;
		this.distance = distance;
	}

	public long getUpStationId() {
		return upStationId;
	}

	public long getDownStationId() {
		return downStationId;
	}

	public int getDistance() {
		return distance;
	}
}
