package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;
import java.util.stream.Stream;

import static nextstep.subway.exception.CommonExceptionMessage.DISTANCE_NOT_UNDER_ZERO;
import static nextstep.subway.exception.CommonExceptionMessage.OVER_DISTANCE;

@Entity
public class Section extends BaseEntity {

	private final static int DISTANCE_NONE = 0;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "line_id")
	private Line line;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "up_station_id")
	private Station upStation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "down_station_id")
	private Station downStation;

	private int distance;

	protected Section() {
		// empty
	}

	private Section(final Station upStation, final Station downStation, final int distance) {
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public static Section of(final Station upStation, final Station downStation, final int distance) {
		checkDistance(distance);
		return new Section(upStation, downStation, distance);
	}

	private static void checkDistance(final int distance) {
		if (distance <= DISTANCE_NONE) {
			throw new IllegalArgumentException(DISTANCE_NOT_UNDER_ZERO.message());
		}
	}

	public void toLine(final Line line) {
		this.line = line;
		line.addSection(this);
	}

	public void connectUpStationToDownStation(final Section section) {
		reduceDistance(section.distance);
		this.upStation = section.downStation;
	}

	public void connectDownStationToUpStation(final Section section) {
		reduceDistance(section.distance);
		this.downStation = section.upStation;
	}

	public void disconnectDownStation(final Section section) {
		unionDistance(section.distance);
		this.upStation = section.upStation;
	}

	public Stream<Station> streamOfStation() {
		return Stream.of(this.upStation, this.downStation);
	}

	public Station upStation() {
		return this.upStation;
	}

	public Station downStation() {
		return this.downStation;
	}

	private void unionDistance(final int distance) {
		this.distance += distance;
	}

	private void reduceDistance(final int distance) {
		if (this.distance <= distance) {
			throw new IllegalArgumentException(OVER_DISTANCE.message());
		}
		this.distance -= distance;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Section section = (Section)o;
		return distance == section.distance
			   && Objects.equals(id, section.id)
			   && Objects.equals(line, section.line)
			   && Objects.equals(upStation, section.upStation)
			   && Objects.equals(downStation, section.downStation);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, line, upStation, downStation, distance);
	}
}
