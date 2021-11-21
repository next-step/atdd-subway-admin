package nextstep.subway.line.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;

@Entity
public class Line extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private LineName name;

	@Embedded
	private LineColor color;

	@Embedded
	private Sections sections = Sections.empty();

	protected Line() {
	}

	public Line(String name, String color, Long upStationId, Long downStationId, int distance) {
		this.name = LineName.from(name);
		this.color = LineColor.from(color);
		this.sections.add(Section.of(this, upStationId, downStationId, distance));
	}

	public void update(Line line) {
		this.name = line.getName();
		this.color = line.getColor();
	}

	public Long getId() {
		return id;
	}

	public LineName getName() {
		return name;
	}

	public LineColor getColor() {
		return color;
	}

	public Sections getSections() {
		return sections;
	}
}
