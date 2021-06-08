package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.stream.Stream;

@Entity
public class Section extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "line_id")
	private Line line;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "up_station_id")
	private Station upStation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "down_station_id")
	private Station downStation;

	private int distance;

	protected Section() {
		// empty
	}

	public Section(final Station upStation, final Station downStation, final int distance) {
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public void toLine(final Line line) {
		this.line = line;
		line.addSection(this);
	}

	public Stream<Station> streamOfStation() {
		return Stream.of(this.upStation, this.downStation);
	}

}
