package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;

public class SectionResponse {

	private final Long id;

	private final int distance;

	private final Long lineId;

	private final Long upStationId;

	private final Long downStationId;

	public static SectionResponse of(Section section) {
		return new SectionResponse(section);
	}

	private SectionResponse(Section section) {
		this.id = section.getId();
		this.distance = section.getDistance();
		this.lineId = section.getLine().getId();
		this.upStationId = section.getUpStation().getId();
		this.downStationId = section.getDownStation().getId();
	}

	public Long getId() {
		return id;
	}

	public int getDistance() {
		return distance;
	}

	public Long getLineId() {
		return lineId;
	}

	public Long getUpStationId() {
		return upStationId;
	}

	public Long getDownStationId() {
		return downStationId;
	}
}
