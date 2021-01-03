package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;

import javax.persistence.*;

@Embeddable
public class Section {

	Long upStationId;

	Long downStationId;

	int distanceMeter;

	public Section() {
	}

	public Section(Long upStationId, Long downStationId, int distanceMeter) {
		this.upStationId = upStationId;
		this.downStationId = downStationId;
		this.distanceMeter = distanceMeter;
	}
}
