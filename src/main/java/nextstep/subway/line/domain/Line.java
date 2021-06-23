package nextstep.subway.line.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Distance;
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

	public Line(String name, String color) {
		this();
		this.name = name;
		this.color = color;
	}

	public Line(String name, String color, Station upStation, Station downStation, int distance) {
		this.name = name;
		this.color = color;
		this.sections.add(new Section(this, upStation, downStation, new Distance(distance)));
	}

	public void update(Line line) {
		this.name = line.getName();
		this.color = line.getColor();
	}

	public Long getId() {
		return this.id;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public void addSection(Section section) {
		this.sections.add(section);
	}

	public Sections getSections() {
		return this.sections;
	}

	public List<Station> getStations() {
		return this.sections.toStations();
	}

	public void removeStation(Station deleteStation) {
		this.sections.removeSection(this, deleteStation);
	}
}
