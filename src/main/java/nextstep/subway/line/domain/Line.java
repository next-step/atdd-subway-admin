package nextstep.subway.line.domain;

import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.SectionNotFoundException;
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

	private Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	private Line(String name, String color, Station upStation, Station downStation, int distance) {
		this.name = name;
		this.color = color;
		addSection(upStation, downStation, distance);
	}

	public static Line of(String name, String color) {
		return new Line(name, color);
	}

	public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
		return new Line(name, color, upStation, downStation, distance);
	}

	public List<Station> getStationsFromUpTerminal() {
		final Section upTerminalSection = sections.getUpTerminalSection();
		return sections.getAllStationSortedByUpToDownFrom(upTerminalSection);
	}

	public void update(Line line) {
		this.name = line.getName();
		this.color = line.getColor();
	}

	public void addSection(Station upStation, Station downStation, int distance) {
		sections.add(Section.of(this, upStation, downStation, distance));
	}

	public Section findSectionBy(Station upStation, Station downStation, int distance) {
		return sections.findAny(section -> section.equalsUpStation(upStation)
			&& section.equalsDownStation(downStation)
			&& section.getDistance() == distance
		).orElseThrow(SectionNotFoundException::new);
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Line)) {
			return false;
		}
		Line line = (Line)o;
		return Objects.equals(id, line.id) && Objects.equals(name, line.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
}
