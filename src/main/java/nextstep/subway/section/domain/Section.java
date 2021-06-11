package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "section")
public class Section extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "upStationId")
	private Station upStation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "downStationId")
	private Station downStation;

	private Integer distance;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lineId")
	private Line line;


	protected Section() {
	}

	public Section(Station upStation, Station downStation, int distance) {
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public void toLine(Line line) {
		this.line = line;
		line.addSection(this);
	}

	public List<Station> getStations() {
		return Arrays.asList(upStation, downStation);
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
}
