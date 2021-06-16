package nextstep.subway.section.domain;

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

	@ManyToOne(optional = false)
	@JoinColumn(name = "up_station_id")
	private Station upStation;

	@ManyToOne(optional = false)
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

	public boolean isUpStationEqualsUpStation(Section section) {
		return this.upStation.equals(section.upStation);
	}

	public boolean isDownStationEqualsDownStation(Section section) {
		return this.downStation.equals(section.downStation);
	}

	public boolean isUpStationEqualsDownStation(Section section) {
		return this.upStation.equals(section.downStation);
	}

	public boolean isDownStationEqualsUpStation(Section section) {
		return this.downStation.equals(section.upStation);
	}

	//ex
	//A 2 B / B 3 C / C 4 D
	//A 1 E
	//result
	//A 1 E / B 3 C / C 4 D
	//1 < 2
	//E (2-1) B

	//ex
	//A 2 B / B 3 C / C 4 D
	//A 3 E
	//2 < 3
	//A 2 B / B 3 C / C 4 D
	//B (3-2) E
	public void setWhenUpStationEqualsUpStation(Section section) {
		if (this.distance.isMoreThan(section.distance)) {
			Station tempDownStation = this.downStation;
			this.downStation = section.downStation;
			this.distance = section.distance;
			section.upStation = section.downStation;
			section.downStation = tempDownStation;
			section.distance = this.distance.getDifferenceDistance(section.distance);
		} else {
			section.upStation = this.downStation;
			section.distance = this.distance.getDifferenceDistance(section.distance);
		}
	}

	//ex
	//A 1 E / B 3 C / C 4 D
	//E 1 B
	//A 1 E / E 1 B / C 4 D
	//B 3 C

	//A 1 E / B 3 C / C 4 D
	//E 4 B
	public void setWhenUpStationEqualsDownStation(Section section) {
		Station tempUpStation = this.upStation;
		Station tempDownStation = this.downStation;
		Distance tempDistance = this.distance;
		this.upStation = section.upStation;
		this.downStation = section.downStation;
		this.distance = section.distance;
		section.upStation = tempUpStation;
		section.downStation = tempDownStation;
		section.distance = tempDistance;
	}
	//A 3 B / B 3 C / C 4 D
	//E 1 B
	//A 1 E / B 3 C / C 4 D
	//E (3-1) B

	//A 3 B / B 3 C / C 4 D
	//E 4 B
	//E 4-3 A / B 3 C / C 4 D
	//A 3 B

	public void setWhenDownStationEqualsDownStation(Section section) {
		if (this.distance.isMoreThan(section.distance)) {
			Station tempUpStation = this.upStation;
			Distance tempDistance = this.distance;
			this.upStation = section.upStation;
			this.distance = this.distance.getDifferenceDistance(section.distance);
			section.upStation = tempUpStation;
			section.distance = tempDistance;
		} else {
			this.downStation = section.upStation;
			this.distance = section.distance;
			section.distance = this.distance.getDifferenceDistance(section.distance);
		}
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
