package nextstep.subway.section.domain;

import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.common.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_line"))
	private Line line;

	@ManyToOne
	@JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station"))
	private Station upStation;

	@ManyToOne
	@JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station"))
	private Station downStation;

	@Embedded
	private Distance distance;

	protected Section() {
	}

	protected Section(Line line, Station upStation, Station downStation, int distance) {
		this(line, upStation, downStation, String.valueOf(distance));
	}

	public Section(Line line, Station upStation, Station downStation, String distance) {
		validateDistanceIsNotNull(distance);
		validateDistanceParseDouble(distance);
		validateLineIsNotNull(line);
		validateUpStationIsNotNull(upStation);
		validateDownStationIsNotNull(downStation);
		validateStationsAreNotSame(upStation, downStation);
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = Distance.generate(Integer.parseInt(distance));
	}

	public static Section generate(Line line, Station upStation, Station downStation, String distance) {
		return new Section(line, upStation, downStation, distance);
	}

	private void validateDistanceParseDouble(String distance) {
		try {
			Integer.parseInt(distance);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("간격은 반드시 숫자로 입력되어야 합니다.");
		}
	}

	private void validateDistanceIsNotNull(String distance) {
		if (Objects.isNull(distance)) {
			throw new IllegalArgumentException("간격은 반드시 입력되어야 합니다.");
		}
	}

	private void validateLineIsNotNull(Line line) {
		if (Objects.isNull(line)) {
			throw new IllegalArgumentException("노선 정보는 반드시 입력되어야 합니다.");
		}
	}

	private void validateUpStationIsNotNull(Station upStation) {
		if (Objects.isNull(upStation)) {
			throw new IllegalArgumentException("상행역 정보는 반드시 입력되어야 합니다.");
		}
	}

	private void validateDownStationIsNotNull(Station downStation) {
		if (Objects.isNull(downStation)) {
			throw new IllegalArgumentException("상행역 정보는 반드시 입력되어야 합니다.");
		}
	}

	private void validateStationsAreNotSame(Station upStation, Station downStation) {
		if (upStation.equals(downStation)) {
			throw new IllegalArgumentException("상행역과 하행역은 일치할 수 없습니다.");
		}
	}

	public Long id() {
		return id;
	}

	public Station upStation() {
		return upStation;
	}

	public Station downStation() {
		return downStation;
	}

	public Line line() {
		return line;
	}

	public int distance() {
		return distance.distance();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Section)) {
			return false;
		}
		Section section = (Section)object;
		return Objects.equals(id, section.id)
			&& Objects.equals(line, section.line)
			&& Objects.equals(upStation, section.upStation)
			&& Objects.equals(downStation, section.downStation)
			&& Objects.equals(distance, section.distance);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, line, upStation, downStation, distance);
	}
}
