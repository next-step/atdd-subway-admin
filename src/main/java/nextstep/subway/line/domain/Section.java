package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.exception.AppException;
import nextstep.subway.exception.ErrorCode;
import nextstep.subway.station.domain.Station;

@Entity
public class Section {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	private Station upStation;

	@ManyToOne(fetch = FetchType.EAGER)
	private Station downStation;

	@Column
	private int distance;

	@JoinColumn
	@ManyToOne(fetch = FetchType.LAZY)
	private Line line;

	protected Section() {
	}

	private Section(Long id, Station upStation, Station downStation, int distance) {
		this.id = id;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	private Section(Long id) {
		this.id = id;
	}

	public static Section of(Long id, Station upStation, Station downStation, int distance) {
		return new Section(id, upStation, downStation, distance);
	}

	public static Section of(Long id, Station upStation, Station downStation, int distance, Line line) {
		Section section = new Section(id, upStation, downStation, distance);
		section.line = line;
		return section;
	}

	public static Section of(Long id) {
		return new Section(id);
	}

	public List<Station> getStations() {
		List<Station> stations = new ArrayList<>();
		stations.add(this.upStation);
		stations.add(this.downStation);
		return stations;
	}

	public void updateUpStation(Section frontSection) {
		this.upStation = frontSection.downStation;
		int different = this.distance - frontSection.distance;
		if (different <= 0) {
			throw new AppException(ErrorCode.WRONG_INPUT, "기존 구간 길이보다 크거나 같으면 안됩니다");
		}
		this.distance = different;
	}

	void setLine(Line line) {
		this.line = line;
	}

	public Long getId() {
		return id;
	}

	public Station getUpStation() {
		return upStation;
	}

	public Station getDownStation() {
		return downStation;
	}

	public int getDistance() {
		return distance;
	}

	public Line getLine() {
		return this.line;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Section section = (Section)o;

		return id.equals(section.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
