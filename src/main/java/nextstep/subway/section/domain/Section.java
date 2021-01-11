package nextstep.subway.section.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {
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

	public Section(Line line, Station upStation, Station downStation, int distance) {
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public Long getId() {
		return id;
	}

	public Line getLine() {
		return line;
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

	public void updateUpStation(Station station, int distance) {
		validateDistance(distance);
		this.upStation = station;
		this.distance -= distance;
	}

	public void updateDownStation(Station station, int distance) {
		validateDistance(distance);
		this.downStation = station;
		this.distance -= distance;
	}

	private void validateDistance(int distance) {
		if (distance >= this.distance) {
			throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
		}
	}
}
