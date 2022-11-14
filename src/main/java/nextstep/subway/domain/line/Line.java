package nextstep.subway.domain.line;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import nextstep.subway.domain.station.Station;
import nextstep.subway.dto.line.LineUpdateRequest;

@Entity
public class Line {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 20, unique = true)
	private String name;

	@Column(nullable = false, length = 20)
	private String color;

	@OneToOne
	@JoinColumn(name = "up_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_line_up_station"))
	private Station upStation;

	@OneToOne
	@JoinColumn(name = "down_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_line_down_station"))
	private Station downStation;

	private int distance;

	protected Line() {
	}

	private Line(String name, String color, Station upStation, Station downStation, int distance) {
		this.name = name;
		this.color = color;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
		return new Line(name, color, upStation, downStation, distance);
	}

	public Long getId() {
		return this.id;
	}

	public Station getUpStation() {
		return this.upStation;
	}

	public Station getDownStation() {
		return this.downStation;
	}

	public void updateLine(LineUpdateRequest request) {
		this.name = request.getName();
		this.color = request.getColor();
	}

	public String getName() {
		return this.name;
	}

	public String getColor() {
		return this.color;
	}

	public int getDistance() {
		return this.distance;
	}
}
