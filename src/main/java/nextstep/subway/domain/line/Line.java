package nextstep.subway.domain.line;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.domain.station.Station;

@Entity
public class Line {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 20, unique = true)
	private String name;

	@Column(nullable = false, length = 20)
	private String color;

	@Embedded
	private Sections sections;

	protected Line() {
	}

	private Line(String name, String color, Station upStation, Station downStation, int distance) {
		this.name = name;
		this.color = color;
		this.sections = Sections.initialSections(new Section(this, upStation, downStation, distance));
	}

	public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
		return new Line(name, color, upStation, downStation, distance);
	}

	public Long getId() {
		return this.id;
	}

	public void updateLine(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return this.name;
	}

	public String getColor() {
		return this.color;
	}

	public Sections getSections() {
		return this.sections;
	}

	public List<Station> allStations() {
		return sections.allStations();
	}

	public void addSection(Section section) {
		sections.add(section);
	}
}
