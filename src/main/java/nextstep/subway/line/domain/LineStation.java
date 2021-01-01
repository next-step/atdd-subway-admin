package nextstep.subway.line.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class LineStation extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "station_id")
	private Station station;

	@ManyToOne
	@JoinColumn(name = "line_id")
	private Line line;

	public LineStation() {
	}

	public LineStation(Line line, Station station) {
		this.line = line;
		this.station = station;
	}

	public Long getPreStationId() {
		return 0L;
	}

	public Long getStationId() {
		return 0L;
	}

	public void updatePreStationTo(Long stationId) {

	}

	public boolean isSame(LineStation lineStation) {
		return true;
	}

	public Station getStation() {
		return station;
	}

	public Line getLine() {
		return line;
	}
}