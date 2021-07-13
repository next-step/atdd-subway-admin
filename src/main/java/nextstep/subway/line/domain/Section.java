package nextstep.subway.line.domain;

import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.exception.InvalidDistanceException;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "line_id")
	private Line line;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "up_station_id")
	private Station upStation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "down_station_id")
	private Station downStation;

	private int distance;

	protected Section() {

	}

	public Section(Line line, Station upStation, Station downStation, int distance) {
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
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

	public List<Station> getStations() {
		return Arrays.asList(upStation, downStation);
	}

	public void addLine(Line line) {
		this.line = line;
	}

	public void updateUpStation(Station station, int newDistance) {
		if(this.distance <= newDistance) {
			throw new InvalidDistanceException();
		}
		upStation = station;
		this.distance -= newDistance;
	}

	public void updateDownStation(Station station, int newDistance) {
		if(this.distance <= newDistance) {
			throw new InvalidDistanceException();
		}
		downStation = station;
		this.distance -= newDistance;
	}
}
