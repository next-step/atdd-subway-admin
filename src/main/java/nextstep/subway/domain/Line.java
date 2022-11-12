package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String color;

	@Embedded
	private LineStations lineStations = new LineStations();

	protected Line() {
	}

	public Line(String name, String color, Station upStation, Station downStation, Integer distance) {
		this.name = name;
		this.color = color;
		addStation(upStation, downStation, distance);
	}

	public void addStation(Station upStation, Station downStation, Integer distance) {
		lineStations.addStation(this, upStation, downStation, distance);
	}

	public void update(String name, String color) {
		this.name = name;
		this.color = color;
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

	public LineStations getLineStations() {
		return lineStations;
	}
}
