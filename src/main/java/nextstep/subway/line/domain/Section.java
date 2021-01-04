package nextstep.subway.line.domain;

import java.util.Arrays;
import java.util.List;

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

	public Section() {
	}

	public Section(Station upStation, Station downStation, int distance) {
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public List<Station> getStations(){
		return Arrays.asList(upStation, downStation);
	}
}
