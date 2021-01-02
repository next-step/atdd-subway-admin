package nextstep.subway.line.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.StationType;
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

	@Column
	@Enumerated(EnumType.STRING)
	private StationType stationType;

	public LineStation() {
	}

	public LineStation(Line line, Station station, StationType stationType) {
		this.line = line;
		this.station = station;
		this.stationType = stationType;
	}

	public Long getPreStationId() {

		return 0L;

	}

	public Long getStationId() {
		return this.station.getId();
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

	public StationType getStationType() {
		return stationType;
	}

	public void updateNoneStationType() {
		this.stationType  = StationType.NONE;
	}

	public void updateUpStationType() {
		this.stationType = StationType.UP_STATION;
	}

	public void updateDownStationType() {
		this.stationType = StationType.DOWN_STATION;
	}
}