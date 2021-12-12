package nextstep.subway.section.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Line line;

	@ManyToOne(fetch = FetchType.LAZY)
	private Station upStation;

	@ManyToOne(fetch = FetchType.LAZY)
	private Station downStation;

	private int distance;

	protected Section() {
	}

	private Section(Station upStation, Station downStation, int distance) {
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public static Section of(Station upStation, Station downStation, int distance) {
		return new Section(upStation, downStation, distance);
	}

	public Station getUpStation() {
		return upStation;
	}

	public Station getDownStation() {
		return downStation;
	}
}
