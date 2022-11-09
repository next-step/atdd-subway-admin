package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Line extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String color;

	@ManyToOne
	@JoinColumn(name = "down_station_id")
	private Station upStation;

	@ManyToOne
	@JoinColumn(name = "up_station_id")
	private Station downStation;

	protected Line() {
	}

	public Line(String name, String color, Station upStation, Station downStation) {
		this.name = name;
		this.color = color;
		setStation(upStation, downStation);
	}

	public void setStation(Station upStation, Station downStation) {
		this.upStation = upStation;
		this.downStation = downStation;
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

	public Station getUpStation() {
		return upStation;
	}

	public Station getDownStation() {
		return downStation;
	}
}
