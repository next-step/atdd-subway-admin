package nextstep.subway.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
	private Line line;

	@ManyToOne
	@JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_up_station"))
	private Station upStation;

	@ManyToOne
	@JoinColumn(name = "downStation_id", foreignKey = @ForeignKey(name = "fk_down_station"))
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

	public boolean isUpStation(Station station) {
		return this.upStation.equals(station);
	}

	public boolean isDownStation(Station station) {
		return this.downStation.equals(station);
	}

	public boolean isDuplicated(Station upStation, Station downStation) {
		return isEqualUpStationAndDownStation(upStation, downStation)
			|| isEqualUpStationAndDownStation(downStation, upStation);
	}

	public boolean isInclude(Station station) {
		return isUpStation(station) || isDownStation(station);
	}

	public boolean isEqualOrLongerThan(int distance) {
		return this.distance >= distance;
	}

	private boolean isEqualUpStationAndDownStation(Station upStation, Station downStation) {
		return isUpStation(upStation) && isDownStation(downStation);
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		Section section = (Section)o;
		return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(line,
			section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation,
			section.downStation);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, line, upStation, downStation, distance);
	}
}
