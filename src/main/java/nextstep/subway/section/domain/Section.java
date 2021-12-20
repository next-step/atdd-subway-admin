package nextstep.subway.section.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Line line;

	@ManyToOne
	private Station upStation;

	@ManyToOne
	private Station downStation;

	private int distance;

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
		if (!line.getSections().contains(this)) {
			line.addSection(this);
		}
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

	public void setUpStation(Station upStation) {
		this.upStation = upStation;
	}

	public void setDownStation(Station downStation) {
		this.downStation = downStation;
	}

	public boolean isSameUpstation(Section section) {
		return upStation.equals(section.upStation);
	}

	public boolean isSameDownStation(Section section) {
		return downStation.equals(section.downStation);
	}

	public boolean isSameStation(Section section) {
		return isSameUpstation(section) && isSameDownStation(section);
	}

	public int diffDistance(Section section) {
		return distance - section.distance;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Section section = (Section)o;
		return Objects.equals(id, section.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
