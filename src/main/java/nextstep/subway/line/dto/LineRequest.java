package nextstep.subway.line.dto;

import java.util.Objects;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

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

	public Long getUpStationId() {
		if (Objects.isNull(upStationId)) {
			return null;
		}
		return Long.parseLong(upStationId);
	}

	public Long getDownStationId() {
		if (Objects.isNull(downStationId)) {
			return null;
		}
		return Long.parseLong(downStationId);
	}

	public String getDistance() {
		return distance;
	}

	public Line toLine() {
		return new Line(name, color);
	}

	public Line toLine(Station upStation, Station downStation) {
		return new Line(name, color, upStation, downStation, distance);
	}
}
