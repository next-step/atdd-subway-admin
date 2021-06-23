package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

public class SectionRequest {

	private Long upStationId;         // 상행역 아이디

	private Long downStationId;       // 하행역 아이디

	private int distance;             // 거리

	protected SectionRequest() {}

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

	public Section toSection(Line line, Station up, Station down) {
		return new Section(line, up, down, distance);
	}
}
