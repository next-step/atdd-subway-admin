package nextstep.subway.section.domain;

import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int distance;

	@ManyToOne
	private Line line;

	@OneToOne
	private Station upStation;

	@OneToOne
	private Station downStation;

	protected Section() {}

	public Section(Line line, Station upStation, Station downStation, int distance) {
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public Line getLine() {
		return line;
	}

	public Stream<Station> stations() {
		return Stream.of(upStation, downStation);
	}

	public Long getId() {
		return id;
	}

	public int getDistance() {
		return distance;
	}

	public Station getUpStation() {
		return upStation;
	}

	public Station getDownStation() {
		return downStation;
	}
}
