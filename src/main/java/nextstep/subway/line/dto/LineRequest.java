package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class LineRequest {
	private String name;
	private String color;
	private Long upStationId;
	private Long downStationId;
	private int distance;

	public LineRequest() {
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
		return this.distance;
	}

	public Line toLine(Station upStation, Station downStation) {
		Section section = new Section(null, upStation, downStation, new Distance(distance));
		Line line =new Line(name, color);
		line.addSection(section);
		return line;
	}
}
