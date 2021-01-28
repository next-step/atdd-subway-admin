package nextstep.subway.line.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import nextstep.subway.station.domain.Station;

@Entity
public class Section implements Comparable<Section> {
	public static final String NO_EXIST_LINE = "등록 가능한 노선이 없습니다.";
	public static final String NO_EXISTS_UP_STATION = "등록 가능한 상행역이 없습니다.";
	public static final String NO_EXIST_DOWN_STATION = "등록 가능한 하행역이 없습니다.";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Line line;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Station upStation;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Station downStation;

	private Distance distance;

	protected Section() {
	}

	private Section(SectionBuilder builder) {
		this.line = builder.line;
		this.upStation = builder.upStation;
		this.downStation = builder.downStation;
		this.distance = builder.distance;
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

	public boolean isSameUpStation(Section section) {
		return upStation.equals(section.getUpStation());
	}

	public void updateUpStation(Section section) {
		this.upStation = section.getDownStation();
		this.distance = this.distance.minus(section.distance);
	}

	public Station getDownStation() {
		return downStation;
	}

	public boolean isSameDownStation(Section section) {
		return downStation.equals(section.getDownStation());
	}

	public void updateDownStation(Section section) {
		this.downStation = section.getUpStation();
		this.distance = this.distance.minus(section.distance);
	}

	@Override
	public int compareTo(Section section) {
		if (this.downStation.equals(section.downStation)) {
			return 0;
		}

		if (this.upStation.equals(section.downStation)) {
			return 1;
		}

		return -1;
	}

	public static SectionBuilder builder() {
		return new SectionBuilder();
	}

	public static final class SectionBuilder {
		private Line line;
		private Station upStation;
		private Station downStation;
		private Distance distance;

		private SectionBuilder() {
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

		public SectionBuilder distance(int distance) {
			this.distance = new Distance(distance);
			return this;
		}

		public Section build() {
			validate();
			return new Section(this);
		}

		private void validate() {
			if (Objects.isNull(this.line)) {
				throw new IllegalArgumentException(NO_EXIST_LINE);
			}

			if (Objects.isNull(this.upStation)) {
				throw new IllegalArgumentException(NO_EXISTS_UP_STATION);
			}

			if (Objects.isNull(this.downStation)) {
				throw new IllegalArgumentException(NO_EXIST_DOWN_STATION);
			}
		}
	}
}
