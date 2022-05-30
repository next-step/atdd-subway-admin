package nextstep.subway.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionRequest {
	private Long upStationId;
	private Long downStationId;
	private int distance;

	public Long getUpStationId() {
		return upStationId;
	}

	public Long getDownStationId() {
		return downStationId;
	}

	public int getDistance() {
		return distance;
	}
	
	public Section toSection(Station upStation, Station downStation) {
		return new Section(upStation, downStation, distance);
	}
}
