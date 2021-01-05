package nextstep.subway.line.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

/**
 * @author : byungkyu
 * @date : 2021/01/04
 * @description :
 **/
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

	private int distance;

	protected Section() {
	}

	public Section(Station upStation, Station downStation, int distance) {
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public List<Station> getStations() {
		return Arrays.asList(upStation, downStation);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
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
