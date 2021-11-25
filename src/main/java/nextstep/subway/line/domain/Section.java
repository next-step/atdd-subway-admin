package nextstep.subway.line.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.ErrorCode;
import nextstep.subway.line.exception.SectionException;
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
		return distance;
	}

	public void validSection(Section section) {
		validNotInStations(section);
		validSameStation(section);
		isInDistance(section);
	}

	private void isInDistance(Section section) {
		if (distance.getDistance() < section.distance.getDistance()
			|| distance.getDistance() == section.distance.getDistance()) {
			throw new SectionException(ErrorCode.VALID_DISTANCE_ERROR);
		}
	}

	public boolean isSameUpStation(Station station) {
		return upStation.equals(station);
	}

	public boolean isSameDownStation(Station station) {
		return downStation.equals(station);
	}

	public boolean isSameUpDownStation(Section compareSection) {
		return isSameUpStation(compareSection.upStation) && isSameDownStation(compareSection.downStation);
	}

	private void validSameStation(Section compareSection) {
		if (isSameUpDownStation(compareSection)) {
			throw new SectionException(ErrorCode.VALID_SAME_STATION_ERROR);
		}
	}

	private boolean isInStations(Section compareSection) {
		return isSameUpStation(compareSection.upStation) || isSameDownStation(compareSection.downStation);
	}

	public void validNotInStations(Section section) {
		if (!isInStations(section)) {
			throw new SectionException(ErrorCode.VALID_NOT_IN_STATIONS_ERROR);
		}
	}

	public void reSettingSection(Section expectSection) {
		if (isSameUpStation(expectSection.upStation)) {
			this.upStation = expectSection.downStation;
		}

		if (isSameDownStation(expectSection.downStation)) {
			this.downStation = expectSection.upStation;
		}

		this.distance.divideDistance(expectSection.distance);
	}

	public void removeSection(Section removeSection) {
		if (isSameUpStation(removeSection.downStation)) {
			this.upStation = removeSection.upStation;
		}

		if (isSameDownStation(removeSection.upStation)) {
			this.downStation = removeSection.downStation;
		}

		this.distance.plusDistance(removeSection.distance);
	}
}
