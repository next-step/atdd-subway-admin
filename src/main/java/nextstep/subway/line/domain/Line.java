package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private LineName name;

	@Column
	private LineColor color;

	@OneToMany(mappedBy = "line", fetch = FetchType.LAZY)
	private List<Section> sections = new ArrayList<>();

	public Line() {
	}

	public Line(String name, String color) {
		this.name = LineName.from(name);
		this.color = LineColor.from(color);
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
}
