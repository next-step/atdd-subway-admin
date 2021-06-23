package nextstep.subway.line.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class LineStation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	private Line line;

	@OneToOne
	private Station station;

	public LineStation() {}

	public LineStation(Line line, Station station) {
		this.line = line;
		this.station = station;
	}

	public Long getPreStationId() {
		return 1L;
	}

	public Long getStationId() {
		return 1L;
	}

	public void updatePreStationTo(Long stationId) {

	}

	public boolean isSame(LineStation lineStation) {
		return lineStation.station == this.station && lineStation.line == this.line;
	}

	@Override
	public String toString() {
		return "LineStation{" +
			"line=" + line +
			", station=" + station +
			'}';
	}
}
