package nextstep.subway.section.domain;

import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.exception.SubwayLogicException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "line_id")
	private Line line;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "up_station_id")
	private Station upStation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "down_station_id")
	private Station downStation;

	@Embedded
	private Distance distance;

	protected Section() {
	}

	public Section(Line line, Station upStation, Station downStation, int distance) {
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = new Distance(distance);
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

	public Distance getDistance() {
		return this.distance;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Section section = (Section)o;
		return Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation)
			&& Objects.equals(downStation, section.downStation) && Objects.equals(distance,
			section.distance);
	}

	@Override
	public int hashCode() {
		return Objects.hash(line, upStation, downStation, distance);
	}

	@Override
	public String toString() {
		return "Section{" +
			"id=" + id +
			", line=" + line +
			", upStationId=" + upStation.getId() +
			", downStationId=" + downStation.getId() +
			", distance=" + distance +
			'}';
	}

	public void validateSectionDistance(Section newSection) {
		if (this.isShorter(newSection)) {
			throw new SubwayLogicException("기존 구간의 길이가 추가하려는 구간보다 짧습니다.");
		}
	}

	private boolean isShorter(Section newSection) {
		return this.distance.isShorter(newSection.getDistance());
	}

	public void rebuildStation(Section newSection) {
		this.validateSectionDistance(newSection);
		this.distance = this.distance.minus(newSection.getDistance());

		if (this.isBuildableUpStation(newSection)) {
			this.upStation = newSection.getDownStation();
			return;
		}

		if (this.isBuildableDownStation(newSection)) {
			this.downStation = newSection.getUpStation();
		}
	}

	public boolean isBuildable(Section newSection) {
		return this.isBuildableDownStation(newSection) || this.isBuildableUpStation(newSection);
	}

	private boolean isBuildableDownStation(Section newSection) {
		return this.downStation.equals(newSection.getDownStation());
	}

	private boolean isBuildableUpStation(Section newSection) {
		return this.upStation.equals(newSection.getUpStation());
	}
}
