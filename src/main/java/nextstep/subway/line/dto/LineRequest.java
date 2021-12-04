package nextstep.subway.line.dto;

import java.util.Collections;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
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
		return distance;
	}

	public Line toLine(Station station1, Station station2) {
		Section section = new Section(station1, station2, distance);
		Sections sections = new Sections(Collections.singletonList(section));
		Line line = new Line(name, color, sections);
		section.toLine(line);
		return line;
	}
}
