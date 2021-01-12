package nextstep.subway.section.domain;

import javax.persistence.Embedded;
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

	@Embedded
	private Distance distance;

	protected Section() {
	}

	public Section(SectionBuilder sectionBuilder) {
		this.line = sectionBuilder.line;
		this.upStation = sectionBuilder.upStation;
		this.downStation = sectionBuilder.downStation;
		this.distance = sectionBuilder.distance;
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

	public Distance getDistance() {
		return distance;
	}

	public static class SectionBuilder {
		private Line line;
		private Station upStation;
		private Station downStation;
		private Distance distance;

		public SectionBuilder() {
		}

		public SectionBuilder line(Line line) {
			this.line = line;
			return this;
		}

		public SectionBuilder upStation(Station upStation) {
			this.upStation = upStation;
			return this;
		}

		public SectionBuilder downStation(Station downStation) {
			this.downStation = downStation;
			return this;
		}

		public SectionBuilder distance(Distance distance) {
			this.distance = distance;
			return this;
		}

		public Section build() {
			return new Section(this);
		}
	}

	public void addUpStation(Station station, Distance distance) {
		this.upStation = station;
		this.distance = new Distance(this.distance.subtractDistance(distance));
	}

	public void addDownStation(Station station, Distance distance) {
		this.downStation = station;
		this.distance = new Distance(this.distance.subtractDistance(distance));
	}

	public void removeDownStation(Station station, Distance distance) {
		this.downStation = station;
		this.distance = new Distance(this.distance.addDistance(distance));
	}

	public boolean isSameUpStation(Station station) {
		return this.upStation.getId().equals(station.getId());
	}

	public boolean isSameDownStation(Station station) {
		return this.downStation.getId().equals(station.getId());
	}
}
