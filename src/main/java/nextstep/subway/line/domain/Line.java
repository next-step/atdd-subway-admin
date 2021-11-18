package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {
	@Column(unique = true)
	private String name;
	private String color;

	@Embedded
	private Sections sections = new Sections();

	protected Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public Line(String name, String color, Station upStation, Station downStation, int distance) {
		this.name = name;
		this.color = color;
		this.sections.add(new Section(this, upStation, downStation, distance));
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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Line line = (Line)o;
		return Objects.equals(getId(), line.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
