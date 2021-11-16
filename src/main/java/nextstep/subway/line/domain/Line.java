package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import nextstep.subway.common.BaseEntity;

@Entity
public class Line extends BaseEntity {
	@Column(unique = true)
	private String name;
	private String color;

	public Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public void update(Line line) {
		this.name = line.getName();
		this.color = line.getColor();
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}
}
