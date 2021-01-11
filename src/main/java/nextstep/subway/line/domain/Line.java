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
	private LineStations lineStations = new LineStations();

	protected Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
		Line line = new Line(name, color);
		line.addOrUpdateStation(upStation, downStation, distance);
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
		return lineStations.getStations();
	}

	public void addOrUpdateStation(Station station, Station downStation, int distance) {
		lineStations.addStation(this, station, downStation, distance);
	}

	public int getDistance(Station station1, Station station2) {
		return lineStations.getDistance(station1, station2);
	}

	public void removeStation(Station station) {
		lineStations.removeStation(this, station);
	}
}
