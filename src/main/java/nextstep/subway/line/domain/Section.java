package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import nextstep.subway.line.exception.IllegalSectionException;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.StationNotFoundException;

@Entity
@Table(uniqueConstraints = {
	@UniqueConstraint(columnNames = {"line_id", "up_station_id", "down_station_id"})
})
public class Section {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Line line;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Station upStation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Station downStation;

	private int distance;

	protected Section() {
	}

	private Section(Line line, Station upStation, Station downStation, int distance) {
		validate(line, upStation, downStation);
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public static Section of(Line line, Station upStation, Station downStation, int distance) {
		return new Section(line, upStation, downStation, distance);
	}

	private void validate(Line line, Station upStation, Station downStation) {
		if (null == line) {
			throw new LineNotFoundException();
		}
		if (null == upStation) {
			throw new StationNotFoundException();
		}
		if (null == downStation) {
			throw new StationNotFoundException();
		}
		if (upStation.equals(downStation)) {
			throw new IllegalSectionException();
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Section)) {
			return false;
		}
		Section section = (Section)o;
		return Objects.equals(id, section.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
