package nextstep.subway.dto;

import nextstep.subway.domain.Line;

public class LineRequest {
	private String name;
	private String color;
	private Long upStationId;
	private Long downStationId;

	private LineRequest() {
	}

	public LineRequest(String name, String color, Long upStationId, Long downStationId) {
		this.name = name;
		this.color = color;
		this.upStationId = upStationId;
		this.downStationId = downStationId;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public Long getUpStationId() {
		return upStationId;
	}

	public Long getDownStationId() {
		return downStationId;
	}
}
