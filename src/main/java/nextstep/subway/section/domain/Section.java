package nextstep.subway.section.domain;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "line_id")
	private Line line;

	@ManyToOne
	@JoinColumn(name = "up_station_id")
	private Station upStation;

	@ManyToOne
	@JoinColumn(name = "down_station_id")
	private Station downStation;

	private Integer distance;

	protected Section() {
	}

	public Section(Station upStation, Station downStation, int distance) {
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public void addLine(Line line) {
		this.line = line;
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

	public Integer getDistance() {
		return distance;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Section section = (Section) o;
		return Objects.equals(id, section.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public boolean isLinked(Section newSection) {
		return this.upStation.equals(newSection.downStation)
			  || this.downStation.equals(newSection.upStation);
	}

	public boolean isSame(Section newSection) {
		return this.upStation.equals(newSection.upStation)
			  && this.downStation.equals(newSection.downStation);
	}

	public boolean isInside(Section newSection) {
		return (this.upStation.equals(newSection.upStation)
			  && !this.downStation.equals(newSection.downStation))
			  || (!this.upStation.equals(newSection.upStation)
			  && this.downStation.equals(newSection.downStation));
	}

	public boolean isUpStationEquals(Section newSection) {
		return this.upStation.equals(newSection.upStation);
	}

	public boolean isDownStationEquals(Section newSection) {
		return this.downStation.equals(newSection.downStation);
	}

	public int calculateDistance(Section newSection) {
		return this.distance - newSection.distance;
	}

	public void changeDownStation(Station downStation, int distance) {
		this.downStation = downStation;
		this.distance = distance;
	}

	public void changeUpStation(Station upStation, int distance) {
		this.upStation = upStation;
		this.distance = distance;
	}
}
