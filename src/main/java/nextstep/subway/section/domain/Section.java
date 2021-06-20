package nextstep.subway.section.domain;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "up_station_id")
	private Station upStation;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "down_station_id")
	private Station downStation;

	@Column(nullable = false)
	private Integer distance;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "line_id")
	private Line line;

	protected Section() {
	}

	public Section(Station upStation, Station downStation, Integer distance) {
		if (upStation.equals(downStation)) {
			throw new IllegalArgumentException("시작역과 종료역이 같을 수 없습니다.");
		}

		if (distance <= 0) {
			throw new IllegalArgumentException("거리가 0보다 작거나 같을 수 없습니다.");
		}

		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public void setLine(Line line) {
		if (this.line != null) {
			line.deleteSection(this);
		}
		this.line = line;
		line.addSection(this);
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

	public Integer getDistance() {
		return distance;
	}

	public Line getLine() {
		return line;
	}

	public boolean isEqualToUpStation(Station station) {
		return upStation.equals(station);
	}

	public boolean isEqualToDownStation(Station station) {
		return downStation.equals(station);
	}

	public boolean matchedOnlyOneStationAndIncludedSection(Section addedSection) {
		if (this.equals(addedSection)) {
			return false;
		}
		return addedSection.isEqualToUpStation(upStation) ^ addedSection.isEqualToDownStation(downStation);
	}

	public List<Section> divide(Section addedSection) {
		if (this.distance <= addedSection.getDistance()) {
			throw new IllegalArgumentException("추가할 구간은 기존 구간보다 짧아야 합니다.");
		}

		List<Section> sections = new LinkedList<>();

		if (addedSection.isEqualToUpStation(upStation)) {
			sections.add(addedSection);
			sections.add(new Section(addedSection.getDownStation(), this.downStation,
				this.distance - addedSection.getDistance()));
		}

		if (addedSection.isEqualToDownStation(downStation)) {
			sections.add(
				new Section(this.upStation, addedSection.getUpStation(), this.distance - addedSection.getDistance()));
			sections.add(addedSection);
		}

		return sections;
	}
}
