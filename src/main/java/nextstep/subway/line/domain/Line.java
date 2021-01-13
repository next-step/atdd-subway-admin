package nextstep.subway.line.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;
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
		this.name = name;
		this.color = color;
	}

	public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
		Line line = new Line(name, color);
		if(upStation != null && downStation != null) {
			line.addSection(upStation, downStation, distance);
		}
		return line;
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

	public void update(Line line) {
		this.name = line.getName();
		this.color = line.getColor();
	}

	public List<Station> getStations() {
		return sections.getStations();
	}

	public long addSection(Station upStation, Station downStation, int distance) {
		return sections.add(this, upStation, downStation, distance);
	}

	public void removeStation(Station station) {
		sections.remove(station);
	}

	public int getDistance(Station upStation, Station downStation) {
		return sections.getDistance(upStation, downStation);
	}
}
