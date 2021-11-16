package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import nextstep.subway.line.exception.IllegalSectionException;
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

	private int distance;

	protected Section() {
	}

	public Section(Station upStation, Station downStation) {
		this(upStation, downStation, 0);
	}

	public Section(Station upStation, Station downStation, int distance) {
		validate(upStation, downStation);
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	private void validate(Station upStation, Station downStation) {
		if (null == upStation && null == downStation) {
			throw new IllegalSectionException();
		}
	}

	public boolean isUpTerminal() {
		return null == upStation && null != downStation;
	}

	public boolean isDownTerminal() {
		return null != upStation && null == downStation;
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
		if (!(o instanceof Section))
			return false;
		Section section = (Section)o;
		return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(
			upStation, section.upStation) && Objects.equals(downStation, section.downStation);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, upStation, downStation, distance);
	}
}
