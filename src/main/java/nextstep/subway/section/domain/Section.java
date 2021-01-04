package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.exception.DistanceException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.springframework.data.geo.Distance;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "line_id")
	private Line line;

	@ManyToOne
	private Station upStation;

	@ManyToOne
	private Station downStation;

	@Column
	private int distanceMeter;

	public Section() {
	}

	public Section(int distanceMeter) {
		this.distanceMeter = distanceMeter;
	}

	public Section(Line line, Station upStation, Station downStation, int distance) {
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distanceMeter = distance;
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

	public int getDistanceMeter() {
		return distanceMeter;
	}

	public void addLine(Line line) {
		this.line = line;
	}

	public boolean isUpStationInSection() {
		if (this.upStation == null) {
			return false;
		}
		return true;
	}

	public boolean isUpStationInSection(Station newUpStation) {
		if (this.upStation == null) {
			return false;
		}
		return this.upStation.equals(newUpStation);
	}

	public boolean isDownStationInSection() {
		if (this.downStation == null) {
			return false;
		}
		return true;
	}

	public boolean isDownStationInSection(Station downStation) {
		if (this.downStation == null) {
			return false;
		}
		return this.downStation.equals(downStation);
	}

	public void updateUpStation(Station station, int newDistanceMeter) {
		this.upStation = station;
		this.distanceMeter = updateDistance(newDistanceMeter);
	}

	private int updateDistance(int newDistanceMeter) {
		if (this.distanceMeter != 0 && this.distanceMeter <= newDistanceMeter) {
			throw new DistanceException();
		}
		if (this.distanceMeter != 0) {
			return this.distanceMeter -= newDistanceMeter;
		}
		return this.distanceMeter;
	}

	public void updateDownStation(Station staion, int newDistanceMeter) {
		this.downStation = staion;
		this.distanceMeter = updateDistance(newDistanceMeter);
	}
}
