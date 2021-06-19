package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {
	public static final int MINIMUM_DISTANCE = 0;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "line_id")
	private Line line;

	@ManyToOne
	@JoinColumn(name = "up_station_id")
	private Station upStation;

	@ManyToOne
	@JoinColumn(name = "down_station_id")
	private Station downStation;

	@Column
	private int distance;

	protected Section() {
	}

	public Section(Line line, Station upStation, Station downStaion, int distance) {
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStaion;
		validateDistance(distance);
		this.distance = distance;
	}

	private void validateDistance(int distance) {
		if (distance <= MINIMUM_DISTANCE) {
			throw new IllegalArgumentException("Distance must be positive number");
		}
	}

	public Long getId() {
		return id;
	}

	public Line getLine() {
		return line;
	}

	public Station getUpStation() {
		return upStation;
	}

	public Station getDownStation() {
		return downStation;
	}

	public int getDistance() {
		return distance;
	}

	public void setLine(Line line) {
		this.line = line;
	}

	public boolean hasSameUpStation(Station station) {
		return this.upStation.equals(station);
	}

	public boolean hasSameDownStation(Station station) {
		return this.downStation.equals(station);
	}
}
