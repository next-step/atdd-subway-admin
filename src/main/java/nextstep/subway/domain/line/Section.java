package nextstep.subway.domain.line;

import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.domain.line.vo.Distance;
import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.InvalidSectionAddException;

@Entity
public class Section {

	private static final String INVALID_SECTION_DISTANCE_MESSAGE = "구간의 거리는 0 이하일 수 없습니다.";
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
	private Line line;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_to_up_station"))
	private Station upStation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_to_down_station"))
	private Station downStation;

	@Embedded
	private Distance distance;

	protected Section() {
	}

	public Section(Line line, Station upStation, Station downStation, Integer distance) {
		validate(distance);
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = new Distance(distance);
	}

	private static void validate(Integer distance) {
		if (distance <= 0) {
			throw new InvalidSectionAddException(INVALID_SECTION_DISTANCE_MESSAGE);
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

	public Integer getDistance() {
		return distance.getValue();
	}

	public boolean isSameUpStation(Section newSection) {
		return isSameStation(this.upStation, newSection.upStation);
	}

	public boolean isSameDownStation(Section newSection) {
		return isSameStation(this.downStation, newSection.downStation);
	}

	private boolean isSameStation(Station station, Station newStation) {
		return station.equals(newStation);
	}

	public void replaceUpStation(Section newSection) {
		this.upStation = newSection.downStation;
		this.distance = this.distance.subtract(newSection.distance);
	}

	public void replaceDownStation(Section newSection) {
		this.downStation = newSection.upStation;
		this.distance = this.distance.subtract(newSection.distance);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Section section = (Section)o;

		if (!Objects.equals(id, section.id))
			return false;
		if (!Objects.equals(upStation, section.upStation))
			return false;
		if (!Objects.equals(downStation, section.downStation))
			return false;
		return Objects.equals(distance, section.distance);
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (upStation != null ? upStation.hashCode() : 0);
		result = 31 * result + (downStation != null ? downStation.hashCode() : 0);
		result = 31 * result + (distance != null ? distance.hashCode() : 0);
		return result;
	}

	public void connect(Section sectionByUpStation) {
		this.downStation = sectionByUpStation.downStation;
		this.distance = this.distance.add(sectionByUpStation.distance);
	}
}
