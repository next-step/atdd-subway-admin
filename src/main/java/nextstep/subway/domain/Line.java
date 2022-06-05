package nextstep.subway.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class Line extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String name;

	private String color;

	@OneToOne
	@JoinColumn(name = "upStationId", foreignKey = @ForeignKey(name = "fk_up_station"))
	private Station upStation;

	@OneToOne
	@JoinColumn(name = "downStationId", foreignKey = @ForeignKey(name = "fk_down_station"))
	private Station downStation;

	protected Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public Line(String name, String color, Station upStation, Station downStation) {
		this.name = name;
		this.color = color;
		this.upStation = upStation;
		this.downStation = downStation;
	}

	public void setUpStation(Station upStation) {
		this.upStation = upStation;
	}

	public void setDownStation(Station downStation) {
		this.downStation = downStation;
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
