package nextstep.subway.line.domain;

import java.util.Objects;
import java.util.stream.Stream;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "line_id")
	private Line line;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "up_station_id")
	private Station up;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "down_station_id")
	private Station down;

	@Embedded
	private Distance distance;

	protected Section() {
	}

	public Section(final Line line, final Station up, final Station down, final Integer distance) {
		this.line = line;
		this.up = up;
		this.down = down;
		this.distance = new Distance(distance);
	}

	public Long getId() {
		return id;
	}

	public Line getLine() {
		return line;
	}

	public Station getUp() {
		return up;
	}

	public Station getDown() {
		return down;
	}

	public Distance getDistance() { return distance; }

	public Stream<Station> getStations() {
		return Stream.of(up, down);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		final Section section = (Section)o;
		return Objects.equals(id, section.id) && Objects.equals(line, section.line) && Objects
			.equals(up, section.up) && Objects.equals(down, section.down);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, line, up, down);
	}

	@Override
	public String toString() {
		return "Section{" +
			"id=" + id +
			", line=" + line +
			", up=" + up +
			", down=" + down +
			", distance=" + distance +
			'}';
	}

	public void update(final Station up, final Station down, final Distance distance) {
		this.up = up;
		this.down = down;
		this.distance = distance;
	}
}
