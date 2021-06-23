package nextstep.subway.section.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.google.common.collect.Lists;

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

	public boolean contains(Station station) {
		return station == upStation || downStation == station;
	}

	public List<Station> stations() {
		return Lists.newArrayList(
			upStation, downStation
		);
	}

	public Line getLine() {
		return line;
	}

	public Long getId() {
		return id;
	}

	public int getDistance() {
		return distance;
	}
}
