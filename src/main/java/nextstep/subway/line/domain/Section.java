package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
@Table(name = "section")
public class Section extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "line_id", nullable = false)
	private Line line;

	@ManyToOne
	@JoinColumn(name = "front_station_id", nullable = false)
	private Station front;

	@ManyToOne
	@JoinColumn(name = "back_station_id", nullable = false)
	private Station back;

	@Embedded
	private Distance distance;

	public Section() {
	}

	public Section(Line line, Station front, Station back, int distance) {
		this.line = line;
		this.front = front;
		this.back = back;
		this.distance = new Distance(distance);
	}

	public Long getId() {
		return id;
	}

	public Line getLine() {
		return line;
	}

	public Station getFront() {
		return front;
	}

	public Station getBack() {
		return back;
	}

	public Distance getDistance() {
		return distance;
	}
}
