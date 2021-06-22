package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.StationGroup;

public class LineRequest {
	private String name;
	private String color;
	private String upStationId;
	private String downStationId;
	private String distance;

	protected LineRequest() {
	}

	public LineRequest(String name, String color) {
		this(name, color, null, null, null);
	}

	public LineRequest(String name, String color, String upStationId, String downStationId, String distance) {
		this.name = name;
		this.color = color;
		this.upStationId = upStationId;
		this.downStationId = downStationId;
		this.distance = distance;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public String getUpStationId() {
		return upStationId;
	}

	public String getDownStationId() {
		return downStationId;
	}

	public String getDistance() {
		return distance;
	}

	public Line toLine() {
		return new Line(name, color);
	}

	public Line toLine(StationGroup stationGroupToAdd) {
		return new Line(name, color);
	}
}
