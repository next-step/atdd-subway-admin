package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;

@Entity
public class Line extends BaseEntity {

	@Column(name = "name", unique = true)
	private String name;
	@Column(name = "color")
	private String color;

	@Embedded
	private Sections sections = new Sections();

	protected Line() {
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

	public Sections getSections() {
		return sections;
	}

	public void addSection(Section section) {
		this.sections.addSection(section);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Line line = (Line)o;
		return Objects.equals(name, line.name) && Objects.equals(color, line.color) && Objects
			.equals(sections, line.sections);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, color, sections);
	}

	@Override
	public String toString() {
		return "Line{" +
			"id=" + id +
			", createdDate=" + createdDate +
			", modifiedDate=" + modifiedDate +
			", name='" + name + '\'' +
			", color='" + color + '\'' +
			'}';
	}
}
