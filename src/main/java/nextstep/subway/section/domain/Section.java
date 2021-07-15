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

	@Override
	public String toString() {
		return "Section{" +
			"id=" + id +
			", distance=" + distance +
			", line=" + line +
			", upStation=" + upStation +
			", downStation=" + downStation +
			'}';
	}

	public boolean containsNoneStations(Section newSection) {
		boolean hasDownStation = hasStation(newSection.getDownStation());
		boolean hasUpStation = hasStation(newSection.getUpStation());
		return !hasDownStation && !hasUpStation;
	}

	public boolean containsOneStation(Section newSection) {
		boolean hasDownStation = hasStation(newSection.getDownStation());
		boolean hasUpStation = hasStation(newSection.getUpStation());
		if (hasDownStation && hasUpStation) {
			return false;
		}
		//noinspection RedundantIfStatement
		if (!hasDownStation && !hasUpStation) {
			return false;
		}
		return true;
	}

	public void appendStations(Section section) throws SectionDistanceNotEnoughException {
		if (containsNoneStations(section)) {
			return;
		}
		if (section.distance >= this.distance) {
			throw new SectionDistanceNotEnoughException(this.getId(), section.getId());
		}
		Station first = null;
		Station second = null;
		Station third = null;
		if (this.upStation == section.downStation) {
			first = section.upStation;
			second = upStation;
			third = downStation;
			swapDistance(section);
		}
		if (this.upStation == section.upStation) {
			first = upStation;
			second = section.downStation;
			third = downStation;
			int gab = this.distance - section.distance;
			this.distance = section.distance;
			section.distance = gab;
		}
		if (this.downStation == section.downStation) {
			first = upStation;
			second = section.upStation;
			third = downStation;
			this.distance = this.distance - section.distance;
		}
		if (this.downStation == section.upStation) {
			first = upStation;
			second = downStation;
			third = section.downStation;
		}
		this.upStation = first;
		this.downStation = second;
		section.upStation = second;
		section.downStation = third;
		if (first == null || second == null || third == null) {
			// 발생하지 않는 상황
			throw new RuntimeException();
		}
	}

	private void swapDistance(Section section) {
		int distance = this.distance;
		this.distance = section.distance;
		section.distance = distance;
	}

	private boolean matchedDownStation(Section section) {
		return this.downStation == section.getDownStation();
	}

	private boolean hasStation(Station station) {
		return this.upStation == station || this.downStation == station;
	}

}
