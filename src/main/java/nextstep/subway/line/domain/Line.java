package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String name;
	private String color;
	@Embedded
	private Sections sections = new Sections();

	protected Line() {
	}

	private Line(String name, String color, Station upStation, Station downStation, int distance) {
		this.name = name;
		this.color = color;
		this.addSection(Section.of(upStation, downStation, distance));
	}

	public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
		return new Line(name, color, upStation, downStation, distance);
	}

	public void addSection(Section section) {
		this.sections.addSection(section);
		if (section.getLine() != this) {
			section.setLine(this);
		}
	}

	public void removeSection(Station station) {
		this.sections.removeSection(station);
	}

	public void update(Line line) {
		this.name = line.getName();
		this.color = line.getColor();
	}

	public Long getId() {
		return id;
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

}
