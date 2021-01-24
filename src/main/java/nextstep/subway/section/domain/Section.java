package nextstep.subway.section.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
@Table(
	uniqueConstraints = {
		@UniqueConstraint(
			columnNames = {"line_id", "up_station_id"}
		),
		@UniqueConstraint(
			columnNames = {"line_id", "down_station_id"}
		)
	}
)
public class Section {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Line line;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Station upStation;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
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

	public Line getLine() {
		return line;
	}

	public List<Station> getStations() {
		return Collections.unmodifiableList(Arrays.asList(upStation, downStation));
	}
}
