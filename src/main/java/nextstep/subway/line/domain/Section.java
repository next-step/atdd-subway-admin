package nextstep.subway.line.domain;

import static javax.persistence.FetchType.*;

import java.util.Objects;
import java.util.stream.Stream;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import nextstep.subway.station.domain.Station;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"up_station_id", "down_station_id"}))
public class Section {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = EAGER)
	@JoinColumn(name = "line_id")
	private Line line;

	@ManyToOne(fetch = EAGER)
	@JoinColumn(name = "up_station_id")
	private Station upStation;

	@ManyToOne(fetch = EAGER)
	@JoinColumn(name = "down_station_id")
	private Station downStation;

	@Embedded
	private Distance distance;

	protected Section() { }

	Section(Line line, Station upStation, Station downStation, Distance distance) {
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	boolean isPreviousOf(Section otherSection) {
		return this.downStation.equals(otherSection.upStation);
	}

	boolean isNextOf(Section otherSection) {
		return this.upStation.equals(otherSection.downStation);
	}

	boolean isSameDownStation(Section otherSection) {
		return this.downStation.equals(otherSection.downStation);
	}

	boolean isSameUpStation(Section otherSection) {
		return this.upStation.equals(otherSection.upStation);
	}

	Stream<Station> getStreamOfStations() {
		return Stream.of(upStation, downStation);
	}

	Long getId() {
		return id;
	}

	Distance getDistance() {
		return distance;
	}

	void addSection(Section otherSection) {
		if (isSameDownStation(otherSection)) {
			addToDownDirection(otherSection);
		}
		if (isSameUpStation(otherSection)) {
			addToUpDirection(otherSection);
		}
	}

	private void addToDownDirection(Section otherSection) {
		validateLongerThan(otherSection);
		this.downStation = otherSection.upStation;
		this.distance = distance.minus(otherSection.distance);
	}

	private void addToUpDirection(Section otherSection) {
		validateLongerThan(otherSection);
		this.upStation = otherSection.downStation;
		this.distance = distance.minus(otherSection.distance);
	}

	private void validateLongerThan(Section otherSection) {
		if (isEqualOrShorterThan(otherSection)) {
			throw new IllegalArgumentException("추가할 구간이 기존 구간 보다 같거나 길 수 없습니다.");
		}
	}

	private boolean isEqualOrShorterThan(Section otherSection) {
		return distance.isLessThan(otherSection.distance);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Section section = (Section)o;
		return Objects.equals(getId(), section.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

	@Override
	public String toString() {
		return "Section{" +
			"id=" + id +
			", line=" + line +
			", upStation=" + upStation +
			", downStation=" + downStation +
			", distance=" + distance +
			'}';
	}
}
