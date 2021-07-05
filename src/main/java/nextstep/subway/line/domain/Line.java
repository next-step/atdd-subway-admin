package nextstep.subway.line.domain;

import java.util.List;

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

	public Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public void addStationToSection(Station upStation, Station downStation, int distance) {
		Section upSection = new Section(this, null, upStation, 0);
		Section downSection = new Section(this, upStation, downStation, distance);

		sections.addAll(upSection, downSection);
	}

	public void addNewSection(Section newSection) {
		sections.add(newSection);
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

	public List<Station> getStations() {
		return this.sections.getStations();
	}

	public List<Section> getSections() {
		return this.sections.getSections();
	}
}
