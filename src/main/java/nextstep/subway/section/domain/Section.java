package nextstep.subway.section.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;


@Entity
public class Section {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "line_id")
	private Line line;

	@ManyToOne
	@JoinColumn(name = "station_id")
	private Station station;

	@Embedded
	private Distance distance;

	@ManyToOne
	@JoinColumn(name = "pre_station_id")
	private Station preStation;

	protected Section() {
	}

	public Section(Line line, Station station, int distance, Station preStation) {
		if (preStation != null && distance == 0) {
			throw new IllegalArgumentException("상행 종점외에는 거리가 0일 수 없습니다");
		}
		this.line = line;
		this.station = station;
		this.distance = new Distance(distance);
		this.preStation = preStation;
	}

	public Line getLine() {
		return line;
	}

	public Station getStation() {
		return station;
	}

	public int getDistance() {
		return distance.get();
	}

	public Station getPreStation() {
		return preStation;
	}

	public boolean isPreStationInSection(Station preStation) {
		return isEqualStation(this.preStation, preStation);
	}

	public boolean isStationInSection(Station station) {
		return isEqualStation(this.station, station);
	}

	public boolean isStationInSection(Long stationId) {
		return stationId.equals(this.station.getId());
	}

	public void updatePreStationTo(Station preStation, int distance) {
		this.preStation = preStation;
		this.distance.subtract(distance);
	}

	public void updatePreStationForRemove(Station preStation, int distance) {
		this.preStation = preStation;
		if (preStation == null) {
			this.distance.reset();
			return;
		}
		this.distance.add(distance);
	}

	public void updateStationTo(Station station, int distance) {
		this.station = station;
		this.distance.subtract(distance);
	}

	private boolean isEqualStation(Station origin, Station target) {
		return target.equals(origin);
	}
}
