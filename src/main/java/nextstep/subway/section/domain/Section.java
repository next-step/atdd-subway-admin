package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "line_id")
	private Line line;

	@ManyToOne
	@JoinColumn(name = "up_station_id")
	private Station upStation;

	@ManyToOne
	@JoinColumn(name = "down_station_id")
	private Station downStation;

	@Embedded
	private Distance distance;

	protected Section() {
	}

	public Section(Line line, Station upStation, Station downStation, Distance distance) {
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public Long getId() {
		return this.id;
	}

	public Station getUpStation() {
		return this.upStation;
	}

	public Station getDownStation() {
		return this.downStation;
	}

	public Distance getDistance() {
		return this.distance;
	}

	public void setLine(Line line) {
		if (Objects.nonNull(this.line)) {
			this.line.getSections().remove(this);
		}
		this.line = line;
		line.getSections().add(this);
	}

	public List<Station> toStations() {
		return new LinkedList<>(Arrays.asList(this.upStation, this.downStation));
	}
}
