package nextstep.subway.line.dto;

import com.sun.istack.NotNull;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;

public class LineRequest {
	@NotNull
	private String name;
	@NotNull
	private String color;
	@NotNull
	private Long upStationId;
	@NotNull
	private Long downStationId;
	@NotNull
	private int distance;

	public LineRequest() {
	}

	public LineRequest(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
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
		return upStationId;
	}

	public Long getDownStationId() {
		return downStationId;
	}

	public int getDistance() {
		return distance;
	}

	public Line toLine() {
		return new Line(name, color);
	}

	public Section toSection(Line line, Station upStation, Station downStation) {
		return new Section(line, upStation, downStation, distance);
	}
}
