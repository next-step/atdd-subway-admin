package nextstep.subway.section.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
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

	@Embedded
	private Distance distance;

	public Section(Line line, Station upStation, Station downStation, Distance distance) {
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public void addUpStation(Station station, Distance distance) {
		this.upStation = station;
		this.distance.subtractDistance(distance);
	}

	public void addDownStation(Station station, Distance distance) {
		this.downStation = station;
		this.distance.subtractDistance(distance);
	}

	public void removeDownStation(Station station, Distance distance) {
		this.downStation = station;
		this.distance.addDistance(distance);
	}

	public boolean isSameUpStationId(Station station) {
		return this.upStation.getId().equals(station.getId());
	}

	public boolean isSameDownStationId(Station station) {
		return this.downStation.getId().equals(station.getId());
	}
}
