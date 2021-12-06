package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionRequest {

	private Long upStationId;
	private Long downStationId;
	private int distance;

	private SectionRequest() {
	}

	public Section toSection(Line line) {
		Station upStation = Station.of(upStationId);
		Station downStation = Station.of(downStationId);
		return Section.of(null, upStation, downStation, distance, line);
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
