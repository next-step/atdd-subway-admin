package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

public class Section {
	private Station upStation;
	private Station downStation;
	private int distance;

	private Section() {

	}

	private Section(Station upStation, Station downStation, int distance) {
		throwIfUpStationAndDownStationIsEqual(upStation, downStation);

		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	private void throwIfUpStationAndDownStationIsEqual(Station upStation, Station downStation) {
		if (upStation.equals(downStation)) {
			throw new IllegalArgumentException("상행역과 하행역은 같을 수 없습니다.");
		}
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

	public int getDistance() {
		return distance;
	}
}
