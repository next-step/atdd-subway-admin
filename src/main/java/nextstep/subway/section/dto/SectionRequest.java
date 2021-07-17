package nextstep.subway.section.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionRequest {

	private Long upStationId;

	private Long downStationId;

	private int distance;

	protected SectionRequest() {}

	public SectionRequest(Long upStationId, Long downStationId, int distance) {
		this.upStationId = upStationId;
		this.downStationId = downStationId;
		this.distance = distance;
	}

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

	public Section toSection(Line line, Station up, Station down) {
		return new Section(line, up, down, distance);
	}
}
