package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "line_station")
public class LineStation {
	@Column(name = "id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "station_id", nullable = false)
	private Long stationId;

	@Column(name = "pre_station_id")
	private Long preStationId;

	@Column(name = "distance")
	private int distance;

	protected LineStation() {

	}

	private LineStation(Long id, Long stationId, Long preStationId, int distance) {
		throwIfStationIdAndPreStationIsEqual(stationId, preStationId);

		this.id = id;
		this.stationId = stationId;
		this.preStationId = preStationId;
		this.distance = distance;
	}

	private void throwIfStationIdAndPreStationIsEqual(Long stationId, Long preStationId) {
		if (stationId.equals(preStationId)) {
			throw new IllegalArgumentException("현재역과 이전역은 같을 수 없습니다.");
		}
	}

	public static LineStation of(Long id, Long stationId, Long preStationId, int distance) {
		return new LineStation(id, stationId, preStationId, distance);
	}

	public static LineStation of(Long stationId, Long preStationId, int distance) {
		return LineStation.of(null, stationId, preStationId, distance);
	}

	public Long getId() {
		return id;
	}

	public Long getStationId() {
		return stationId;
	}

	public Long getPreStationId() {
		return preStationId;
	}

	public int getDistance() {
		return distance;
	}

	public void update(Long preStationId, int distance) {
		this.preStationId = preStationId;
		this.distance = distance;
	}

	public boolean hasPrev() {
		return preStationId != null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		LineStation that = (LineStation)o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
