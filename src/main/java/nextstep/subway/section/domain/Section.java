package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

	@ManyToOne(fetch = FetchType.LAZY)
	private Station upStation;

	@ManyToOne(fetch = FetchType.LAZY)
	private Station downStation;

	@Column
	private int distance;

	@JoinColumn
	@ManyToOne(fetch = FetchType.LAZY)
	private Line line;

	protected Section() {
	}

	private Section(Station upStation, Station downStation, int distance) {
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public static Section of(Station upStation, Station downStation, int distance) {
		return new Section(upStation, downStation, distance);
	}

	public void setLine(Line line) {
		this.line = line;
	}

	public Long getId() {
		return id;
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
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Section section = (Section)o;

		if (distance != section.distance)
			return false;
		if (!Objects.equals(id, section.id))
			return false;
		if (!Objects.equals(upStation, section.upStation))
			return false;
		return Objects.equals(downStation, section.downStation);
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (upStation != null ? upStation.hashCode() : 0);
		result = 31 * result + (downStation != null ? downStation.hashCode() : 0);
		result = 31 * result + distance;
		return result;
	}

	public List<Station> getStations() {
		List<Station> stations = new ArrayList<>();
		stations.add(this.upStation);
		stations.add(this.downStation);
		return stations;
	}
}
