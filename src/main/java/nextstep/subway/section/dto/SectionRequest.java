package nextstep.subway.section.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionRequest {
	private Long upStationId;
	private Long downStationId;
	private int distance;

	protected SectionRequest() {
	}

	public SectionRequest(Long upStationId, Long downStationId, int distance) {
		this.upStationId = upStationId;
		this.downStationId = downStationId;
		this.distance = distance;
	}

	public int getDistance() {
		return distance;
	}

	public Long getUpStationId() {
		return upStationId;
	}

	public Long getDownStationId() {
		return downStationId;
	}

	public Section toSection(Line line, Station upStation, Station downStation) {
		return new Section(line, upStation, downStation, distance);
	}
}
