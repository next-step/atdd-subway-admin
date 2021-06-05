package nextstep.subway.line.domain;

import static javax.persistence.FetchType.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import nextstep.subway.station.domain.Station;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"up_station_id", "down_station_id"}))
public class Section {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "line_id")
	private Line line;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "up_station_id")
	private Station upStation;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "down_station_id")
	private Station downStation;

	@Column(nullable = false)
	private Integer distance;

	protected Section() { }

	Section(Line line, Station upStation, Station downStation, Integer distance) {
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	boolean isIn(Line line) {
		return this.line.equals(line);
	}
}
