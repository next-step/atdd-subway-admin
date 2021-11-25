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

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.IllegalSectionDistanceException;
import nextstep.subway.line.exception.IllegalSectionException;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.StationNotFoundException;

@Entity
@Table(uniqueConstraints = {
	@UniqueConstraint(columnNames = {"line_id", "up_station_id", "down_station_id"})
})
public class Section extends BaseEntity {

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
		validate(line, upStation, downStation, distance);
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public static Section of(Line line, Station upStation, Station downStation, int distance) {
		return new Section(line, upStation, downStation, distance);
	}

	private void validate(Line line, Station upStation, Station downStation, int distance) {
		if (null == line) {
			throw new LineNotFoundException();
		}
		validateStations(upStation, downStation);
		validateDistance(distance);
	}

	private void validateStations(Station upStation, Station downStation) {
		validateStation(upStation);
		validateStation(downStation);
		if (upStation.equals(downStation)) {
			throw new IllegalSectionException();
		}
	}

	private void validateStation(Station station) {
		if (null == station) {
			throw new StationNotFoundException();
		}
	}

	private void validateDistance(int distance) {
		if (distance <= 0) {
			throw new IllegalSectionDistanceException();
		}
	}

	public void updateUpStation(Station station, int distance) {
		setUpStation(station);
		setDistance(distance);
	}

	public void updateDownStation(Station station, int distance) {
		setDownStation(station);
		setDistance(distance);
	}

	public boolean equalsUpStation(Station station) {
		return upStation.equals(station);
	}

	public boolean equalsDownStation(Station station) {
		return downStation.equals(station);
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

	private void setUpStation(Station upStation) {
		validateStations(upStation, downStation);
		this.upStation = upStation;
	}

	private void setDownStation(Station downStation) {
		validateStations(upStation, downStation);
		this.downStation = downStation;
	}

	private void setDistance(int distance) {
		validateDistance(distance);
		this.distance = distance;
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
		return Objects.equals(id, section.id) && Objects.equals(line, section.line)
			&& Objects.equals(upStation, section.upStation) && Objects.equals(downStation,
			section.downStation);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, line, upStation, downStation);
	}
}
