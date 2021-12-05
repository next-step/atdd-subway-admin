package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

public class LineUpdateRequest {

	private String name;
	private String color;

	public LineUpdateRequest() {
	}

	public LineUpdateRequest(String name, String color) {
		this.color = color;
		this.name = name;
	}

	public Line toLine() {
		return Line.of(null, name, color);
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

}
