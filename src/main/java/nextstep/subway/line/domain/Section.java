package nextstep.subway.line.domain;

public class Section {
	private final Long upStationId;
	private final Long downStationId;
	private final int distance;

	private Section(Long upStationId, Long downStationId, int distance) {
		throwIfUpStationAndDownStationIsEqual(upStationId, downStationId);

		this.upStationId = upStationId;
		this.downStationId = downStationId;
		this.distance = distance;
	}

	private void throwIfUpStationAndDownStationIsEqual(Long upStationId, Long downStationId) {
		if (upStationId.equals(downStationId)) {
			throw new IllegalArgumentException("상행역과 하행역은 같을 수 없습니다.");
		}
	}

	public static Section of(Long upStationId, Long downStationId, int distance) {
		return new Section(upStationId, downStationId, distance);
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
