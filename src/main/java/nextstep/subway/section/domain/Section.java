package nextstep.subway.section.domain;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

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

	@OneToOne
	@JoinColumn(name = "up_station_id")
	private Station upStation;

	@OneToOne
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
		this.distance = distance;
	}

	public List<Station> getStations() {
		return Arrays.asList(upStation, downStation);
	}

	public Station getUpStation() {
		return upStation;
	}

	public Station getDownStation() {
		return downStation;
	}

	public void setLine(Line line) {
		this.line = line;
	}
}
