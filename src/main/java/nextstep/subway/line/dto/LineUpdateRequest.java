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

	public Line toLine(Long id) {
		return Line.of(id, name, color);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
