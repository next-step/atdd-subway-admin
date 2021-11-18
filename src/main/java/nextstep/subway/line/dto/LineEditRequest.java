package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

public class LineEditRequest {
	private final String name;
	private final String color;

	public LineEditRequest(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public Line toLine() {
		return Line.of(name, color);
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}
}
