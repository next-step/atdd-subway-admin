package nextstep.subway.section.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Section extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Line line;

	@ManyToOne(fetch = FetchType.EAGER)
	private Station upStation;

	@ManyToOne(fetch = FetchType.EAGER)
	private Station downStation;

	private int distance;

	private Section(Line line, Station upStation, Station downStation, int distance) {
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public static Section create(Line line, Station upStation, Station downStation, int distance) {
		return new Section(line, upStation, downStation, distance);
	}

	public void update(Station upstation, Station downStation, int distance) {
		this.upStation = upstation;
		this.downStation = downStation;
		this.distance = distance;
	}
}
