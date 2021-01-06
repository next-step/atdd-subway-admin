package nextstep.subway.section.domain;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Section {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "line_id")
	private Line line;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "up_station_id")
	private Station upStation;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "down_station_id")
	private Station downStation;

	@Setter
	private int distance;

	public List<Station> getStations() {
		return Arrays.asList(upStation, downStation);
	}

	public Section(Line line, Station upStation, Station downStation, int distance) {
		if (upStation.equals(downStation)) {
			throw new RuntimeException("중복된 Station 입니다.");
		}
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public void updateUpStation(Section target) {
		validateDistance(target.getDistance());
		this.distance = distance - target.getDistance();
		this.upStation = target.getDownStation();
	}

	public void updateDownStation(Section target) {
		validateDistance(target.getDistance());
		this.distance = distance - target.getDistance();
		this.downStation = target.getUpStation();
	}

	public boolean isUpStation(Section target) {
		return this.upStation.equals(target.getUpStation());
	}

	public boolean isDownStation(Section target) {
		return this.downStation.equals(target.getDownStation());
	}

	public boolean contains(Station target) {
		return getStations().contains(target);
	}

	private void validateDistance(int distance) {
		if (this.distance <= distance) {
			throw new IllegalArgumentException("추가하는 구간의 거리가 기존구간보더 더 큽니다.");
		}
	}
}
