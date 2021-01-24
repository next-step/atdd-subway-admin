package nextstep.subway.line.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import nextstep.subway.station.domain.Station;

@Entity
public class Section implements Comparable<Section> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Line line;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Station upStation;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
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

	public Line getLine() {
		return line;
	}

	public List<Station> getStations() {
		return Collections.unmodifiableList(Arrays.asList(upStation, downStation));
	}

	public Station getUpStation() {
		return upStation;
	}

	public boolean isUpStation(Station station) {
		return upStation.equals(station);
	}

	public void updateUpStation(Station station, int distance) {
		upStation = station;
		this.distance -= distance;
	}

	public Station getDownStation() {
		return downStation;
	}

	public boolean isDownStation(Station station) {
		return downStation.equals(station);
	}

	public void updateDownStation(Station station, int distance) {
		downStation = station;
		this.distance -= distance;
	}

	public int getDistance() {
		return distance;
	}

	@Override
	public int compareTo(Section section) {
		if (isDownStation(section.downStation)) {
			return 0;
		}

		if (isUpStation(section.downStation)) {
			return 1;
		}

		return -1;
	}
}
