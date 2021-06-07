package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

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
		// empty
	}

	public Section(final Line line, final Station upStation, final Station downStation, final Integer distance) {
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}
}
